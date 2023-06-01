package io.rabobank.ret

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

data class SearchHit(val filterRound: FilterRound, val candidateIndex: Int)

data class FilterRound(val filter: String, val startIndex: Int, val endIndex: Int) {
    val value = filter.substring(startIndex..endIndex)

    override fun toString() = "filter: $filter part: $startIndex..$endIndex value: $value"

    fun continueWithNextPart() = copy(startIndex = startIndex + value.length, endIndex = filter.length - 1)

    fun retryWithSmallerPart() = copy(endIndex = endIndex - 1)

    fun retryFromStartWithSmallerPart() = copy(startIndex = 0, endIndex = endIndex - 1)

    fun isDeadEnd() = startIndex == endIndex && !hasSucceeded()

    fun hasSucceeded() = startIndex == filter.length

    fun hasFailed() = endIndex <= 0
}

/**
 * Match results like IntelliJ
 *
 * This class is used to match a string of characters to a possible candidate, based on partial string matching. The matching copies the behaviour of e.g. searching for classes in IntelliJ.
 *
 * Example: "as" will positively match with "Admin Service" or "admin-service", and so will "adser".
 */
@ApplicationScoped
class IntelliSearch {

    companion object {
        val BREAK_CHARACTER_REGEX = "[\\s_-]+".toRegex()
        val UPPERCASE_REGEX = "(?=\\p{Upper})".toRegex()
    }

    /**
     * Returns whether [filter] matches the [candidate] string, based on partial string matching.
     *
     * Example: [filter] "as" will positively match "Admin Service".
     * @return whether the result is a match.
     */
    fun matches(filter: String, candidate: String): Boolean {
        if (BREAK_CHARACTER_REGEX.replace(candidate, "").contains(BREAK_CHARACTER_REGEX.replace(filter, ""), true)) {
            return true
        }

        val candidateParts = splitCandidateInParts(candidate)

        var filterRound = FilterRound(filter, 0, filter.length - 1)
        var loopRounds = 0

        var lastSearchHit: SearchHit? = null
        val bannedSearches = mutableListOf<SearchHit>()

        filterPartLoop@ while (!filterRound.hasSucceeded() && loopRounds++ < 1000) {
            val filterPart = filterRound.value
            Log.trace("Searching filterPart $filterRound")

            for ((candidateIndex, candidatePart) in candidateParts.withIndex()) {
                val possibleSearchHit = SearchHit(filterRound, candidateIndex)

                if (candidateIndex <= (lastSearchHit?.candidateIndex ?: -1)) {
                    continue
                }

                if (bannedSearches.contains(possibleSearchHit)) {
                    Log.trace("Found banned word $filterPart in $candidatePart with index $candidateIndex")
                    break
                }

                if (candidatePart.startsWith(filterPart, true)) {
                    Log.trace("Found match for $filterRound in $candidatePart with index $candidateIndex")
                    lastSearchHit = possibleSearchHit
                    filterRound = filterRound.continueWithNextPart()
                    continue@filterPartLoop
                }
            }

            if (filterRound.isDeadEnd()) {
                if (lastSearchHit != null) {
                    Log.trace("Adding $lastSearchHit to banned search hits")
                    bannedSearches.add(lastSearchHit)
                    lastSearchHit = null
                }

                filterRound = filterRound.retryFromStartWithSmallerPart()

                Log.trace("Found dead end, setting filter to $filterRound")
                continue
            }

            if (filterRound.hasFailed()) {
                break
            }

            filterRound = filterRound.retryWithSmallerPart()
        }

        return filterRound.hasSucceeded()
    }

    private fun splitCandidateInParts(candidate: String): List<String> {
        val candidateParts = candidate.split(BREAK_CHARACTER_REGEX)

        // If splitting on break characters does not work, fall back to capitals
        if (candidateParts.size <= 1) {
            return candidate.split(UPPERCASE_REGEX)
        }

        return candidateParts
    }
}
