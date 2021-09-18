# Location Simulator

Location SImulator plots points in the maps with intervals based on logical positions.

## Requirements:

- Use the Java 1.8 JDK for running the application.
- Use Maven to support the jar and maven functionality.
## Running

# Library Used:

```java
import okhttp3.OkHttpClient;
import org.json.*;

# major functions used in the program

# returns bearing angle
getBearing(double lat1, double lng1, double lat2, double lng2)

# returns next lat-long points based on the previous points and bearing angle
movePoint(double latitude, double longitude, double distanceInMetres, double bearing)

```

# Steps to run:
1. Enter the origination and destination lat-long in the file under the resource folder with the format as.
   `12.969640,77.752749`\
   `12.983775,77.752448`
2. Run the application using the command as:
  ```java
  mvn clean install -DskipTests
  java -jar target/Location-Simulator.jar
```
3. Output in the command line and paste it into the site [plots](https://www.mapcustomizer.com/#) for the result.

4. Check the screenshot in the location.png

**Note:** there are some bugs which I am unable to figure out in shorter span of time, But I will figure it in future in the [github](https://github.com/Sagar2011) profile.
## Contributed
[Sagar Jain](https://www.linkedin.com/in/sagarjain2010/)