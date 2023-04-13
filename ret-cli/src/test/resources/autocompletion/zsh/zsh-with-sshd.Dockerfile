FROM zshusers/zsh:5.9
RUN apt-get update
RUN apt-get install -y openssh-server
RUN mkdir /var/run/sshd

RUN echo 'root:root' |chpasswd

RUN echo 'PermitRootLogin yes' >> /etc/ssh/sshd_config
RUN sed -i 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

RUN mkdir /root/.ssh
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN chsh -s $(which zsh)

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]