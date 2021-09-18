import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;

public class LocationSimulator {

    // to calculate the bearing angle for getting the direction to calculate next point
    public static double getBearing(double lat1, double lng1, double lat2, double lng2) {

        double dLon = (lng2 - lng1);
        double x = Math.sin(dLon) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        return Math.toDegrees((Math.atan2(x, y)));
    }

    //calculate the next lat long point based on the previous point and bearing angle
    public static String movePoint(double latitude, double longitude, double distanceInMetres, double bearing) {
        double brngRad = toRadians(bearing);
        double latRad = toRadians(latitude);
        double lonRad = toRadians(longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult = asin(sin(latRad) * cos(distFrac) + cos(latRad) * sin(distFrac) * cos(brngRad));
        double a = atan2(sin(brngRad) * sin(distFrac) * cos(latRad), cos(distFrac) - sin(latRad) * sin(latitudeResult));
        double longitudeResult = lonRad + a;

        //precision based on the 7 digit on the lat long
        return ((Math.round(toDegrees(latitudeResult) * 10000000.0) / 10000000.0) + "," + (Math.round(toDegrees(longitudeResult) * 10000000.0) / 10000000.0));
    }

    public static void main(String[] args) throws IOException {

        //input the latlong in this file under resource folder.
        String fileName = "input.txt";
        ClassLoader classLoader = LocationSimulator.class.getClassLoader();

        try {
            File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            List<String> inputs = new ArrayList<>();
            while ((st = br.readLine()) != null) {
                inputs.add(st);
            }

            String orig = inputs.get(0);
            String dest = inputs.get(1);

            TreeSet<String> sets = new TreeSet<>();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/directions/json?origin=" + orig + "&destination=" + dest + "&key=AIzaSyAEQvKUVouPDENLkQlCF6AAap1Ze-6zMos")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray routes = (JSONArray) jsonObject.get("routes");

                JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

                JSONObject dataPoints = legs.getJSONObject(0);
                double distance = Double.parseDouble(dataPoints.getJSONObject("distance").get("value").toString());
                double duration = Double.parseDouble(dataPoints.getJSONObject("duration").get("value").toString());

                double interval = (distance / duration);
                int inter = (int) Math.round(interval) * 10;
                String start_location = dataPoints.getJSONObject("start_location").get("lat").toString() + "," + dataPoints.getJSONObject("start_location").get("lng").toString();
                String end_location = dataPoints.getJSONObject("end_location").get("lat").toString() + "," + dataPoints.getJSONObject("end_location").get("lng").toString();


                JSONArray steps = (JSONArray) dataPoints.get("steps");


                sets.add(orig);
                sets.add(start_location);
                steps.forEach(step ->

                {
                    JSONObject object = (JSONObject) step;
                    String midstart_location = object.getJSONObject("start_location").get("lat").toString() + "," + object.getJSONObject("start_location").get("lng").toString();
                    String midend_location = object.getJSONObject("end_location").get("lat").toString() + "," + object.getJSONObject("end_location").get("lng").toString();
                    int midDist = (int) object.getJSONObject("distance").get("value");
                    double latX = Double.parseDouble(midstart_location.split(",")[0]);
                    double lngX = Double.parseDouble(midstart_location.split(",")[1]);
                    double latY = Double.parseDouble(midend_location.split(",")[0]);
                    double lngY = Double.parseDouble(midend_location.split(",")[1]);
                    double bear = getBearing(latX, lngX, latY, lngY);
                    while (midDist > 0) {
                        String data = movePoint(latX, lngX, inter, bear);
                        sets.add(data);
                        bear = getBearing(Double.parseDouble(data.split(",")[0]), Double.parseDouble(data.split(",")[1]), latY, lngY);
                        midDist = midDist - inter;
                        latX = Double.parseDouble(data.split(",")[0]);
                        lngX = Double.parseDouble(data.split(",")[1]);
                    }

                    sets.add(midstart_location);
                    sets.add(midend_location);

                });
                sets.add(end_location);
                sets.add(dest);
                sets.forEach(System.out::println);
            } else {
                System.out.println("Error in direction API" + response.code());
            }
        } catch (Exception exception) {
            System.out.println("Exception Occured while running location simulator as " + exception.getMessage());
        }
    }
}
