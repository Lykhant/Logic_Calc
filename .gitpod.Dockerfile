FROM gitpod/workspace-full

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh"
RUN bash -c "sdk update <<< y"
RUN bash -c "sdk install java 16.0.2.fx-zulu <<< y""