rm -R javadoc
javadoc src/main/java/cloud/timo/TimoCloud/api/*.java \
src/main/java/cloud/timo/TimoCloud/api/objects/*.java \
src/main/java/cloud/timo/TimoCloud/api/events/*.java \
-exclude */internal/* \
-footer "<a href="https://impressum.timo.cloud">Impressum</a>" \
-d javadoc
