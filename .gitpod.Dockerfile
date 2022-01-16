FROM gitpod/workspace-full[:latest]

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk update <<< y && sdk install java 16.0.2.fx-zulu <<< y"
