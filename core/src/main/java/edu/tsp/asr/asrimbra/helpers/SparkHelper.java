package edu.tsp.asr.asrimbra.helpers;

import spark.Request;
import spark.Route;

import static spark.Spark.halt;

public class SparkHelper {

    public static void checkQueryParamsNullity(Request request, String... params) {
        for (String paramName : params) {
            if(request.queryParams(paramName)==null) {
                String errorMessage = "Bad parameters, " + paramName + " must not be null";
                System.err.println(errorMessage);
                halt(400, errorMessage);
                return;
            }
        }
    }

    public static Route generateOptionRoute(String... methodsAllowed) {
        return (request, response) -> {
            response.header(
                    "Access-Control-Allow-Methods",
                    "OPTIONS, "+String.join(", ", methodsAllowed)
            );
            return "";
        };
    }
}
