# Start spring boot devtools process in the background.
# This is necessary for hot reload.
./gradlew -t :bootJar &

# Next, start the app.
./gradlew bootRun