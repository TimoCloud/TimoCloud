rm -R javadoc
javadoc src/main/java/at/TimoCraft/TimoCloud/api/*.java \
src/main/java/at/TimoCraft/TimoCloud/api/objects/GroupObject.java \
src/main/java/at/TimoCraft/TimoCloud/api/objects/ServerObject.java \
-footer "<a href="https://impressum.timo.cloud">Impressum</a>" \
-d javadoc
