package org.iheartradio.techtalk.domain

import org.iheartradio.techtalk.SQLStatement

object DbFunctions {

    const val FUNCTION_CALC_DIST = "calculate_distance"

    fun createOrReplace() {
        createFunctionCalculateDistance()
    }

    /**
     * /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::                                                                         :*/
    /*::  This routine calculates the distance between two points (given the     :*/
    /*::  latitude/longitude of those points). It is being used to calculate     :*/
    /*::  the distance between two locations using GeoDataSource(TM) Products    :*/
    /*::                                                                         :*/
    /*::  Definitions:                                                           :*/
    /*::    South latitudes are negative, east longitudes are positive           :*/
    /*::                                                                         :*/
    /*::  Passed to function:                                                    :*/
    /*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
    /*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
    /*::    unit = the unit you desire for results                               :*/
    /*::           where: 'M' is statute miles (default)                         :*/
    /*::                  'K' is kilometers                                      :*/
    /*::                  'N' is nautical miles                                  :*/
    /*::  Worldwide cities and other features databases with latitude longitude  :*/
    /*::  are available at https://www.geodatasource.com                         :*/
    /*::                                                                         :*/
    /*::  For enquiries, please contact sales@geodatasource.com                  :*/
    /*::                                                                         :*/
    /*::  Official Web site: https://www.geodatasource.com                       :*/
    /*::                                                                         :*/
    /*::  Thanks to Kirill Bekus for contributing the source code.               :*/
    /*::                                                                         :*/
    /*::         GeoDataSource.com (C) All Rights Reserved 2019                  :*/
    /*::                                                                         :*/
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
     */
    private fun createFunctionCalculateDistance() {
        val sql = "CREATE OR REPLACE FUNCTION $FUNCTION_CALC_DIST(lat1 float, lon1 float, lat2 float, lon2 float, units varchar)\n" +
                "RETURNS float AS \$dist\$\n" +
                "    DECLARE\n" +
                "        dist float = 0;\n" +
                "        radlat1 float;\n" +
                "        radlat2 float;\n" +
                "        theta float;\n" +
                "        radtheta float;\n" +
                "    BEGIN\n" +
                "        IF lat1 = lat2 OR lon1 = lon2\n" +
                "            THEN RETURN dist;\n" +
                "        ELSE\n" +
                "            radlat1 = pi() * lat1 / 180;\n" +
                "            radlat2 = pi() * lat2 / 180;\n" +
                "            theta = lon1 - lon2;\n" +
                "            radtheta = pi() * theta / 180;\n" +
                "            dist = sin(radlat1) * sin(radlat2) + cos(radlat1) * cos(radlat2) * cos(radtheta);\n" +
                "\n" +
                "            IF dist > 1 THEN dist = 1; END IF;\n" +
                "\n" +
                "            dist = acos(dist);\n" +
                "            dist = dist * 180 / pi();\n" +
                "            dist = dist * 60 * 1.1515;\n" +
                "\n" +
                "            IF units = 'K' THEN dist = dist * 1.609344; END IF;\n" +
                "            IF units = 'N' THEN dist = dist * 0.8684; END IF;\n" +
                "\n" +
                "            RETURN dist;\n" +
                "        END IF;\n" +
                "    END;\n" +
                "\$dist\$ LANGUAGE plpgsql;"


        SQLStatement(sql).exec()
    }

}