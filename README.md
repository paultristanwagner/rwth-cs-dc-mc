# Minecraft plugins for the RWTH computer science Discord

## Build and deploy the plugin
The plugin can be build and deployed via gradle.  
Learn, how to install gradle [here](https://gradle.org/install/) or use the gradle wrapper with `./gradlew` instead of `gradle` for the following instructions.    

`gradle jar`  
Will build the plugin jar.  

`gradle deployPlugin`  
Can now be used to deploy the plugin onto the test server.  

Or we can just combine both:  
`gradle jar deployPlugin`

## Running the test server:
`./run_testserver.sh`  
This will download and run the PaperMC server in the directory `testserver` and requires at least Java 17.  
You might need to accept the EULA in the eula.txt file.  

The server can be stopped with the command `stop` and plugins can be reloaded with the command `reload confirm`.  

