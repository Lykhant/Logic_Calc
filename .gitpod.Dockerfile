FROM gitpod/workspace-full

RUN . /home/gitpod/.sdkman/bin/sdkman-init.sh
RUN sdk update <<< y
RUN sdk install java 16.0.2.fx-zulu <<< y
