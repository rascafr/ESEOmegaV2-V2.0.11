/**
 * Copyright (C) 2016 - François LEPAROUX
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.bde_eseo.eseomega.events.tickets.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by François L. on 13/01/2016.
 * Décris une navette
 */
public class ShuttleItem {

    private int idshuttle, remainingSeats, totalSeats;
    private String departure, arrival, departureStr, arrivalStr, idevent, departPlace;

    public ShuttleItem(JSONObject obj) throws JSONException {
        idshuttle = obj.getInt("idshuttle");
        remainingSeats = obj.getInt("restseats");
        totalSeats = obj.getInt("totseats");
        departure = obj.getString("departure");
        arrival = obj.getString("arrival");
        idevent = obj.getString("idevent");
        departPlace = obj.getString("departplace");

        departureStr = "Départ : " + getFrenchDate(departure);
    }

    public boolean correspondsToID(String id) {
        return idevent.equals(id);
    }

    public int getIdshuttle() {
        return idshuttle;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getIdevent() {
        return idevent;
    }

    public String getDepartPlace() {
        return departPlace;
    }

    public String getDepartureStr() {
        return departureStr;
    }

    public Date getParsedDate (String datetime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        Date date = null;
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getFrenchDate (String datetime) {
        Date d = getParsedDate(datetime);
        SimpleDateFormat sdf = new SimpleDateFormat("E dd MMM 'à' HH'h'mm", Locale.FRANCE);
        return sdf.format(d);
    }
}
