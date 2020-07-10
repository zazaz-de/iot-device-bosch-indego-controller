/**
 * Copyright (C) 2020 Oliver Schünemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @since 11.06.2020
 * @version 1.0
 * @author oliver
 */
package de.zazaz.iot.bosch.indego;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author oliver
 *
 */
@JsonPropertyOrder({ "LocationWeather" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationWeather {

	private Weather weather;

	@JsonPropertyOrder({ "location", "forecast", "days" })
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Weather {
		private Location location;
		private Forecast forecast;

		@JsonGetter("location")
		public Location getLocatoin() {
			return location;
		}

		@JsonSetter("location")
		public void setLocation(final Location location) {
			this.location = location;
		}

		/**
		 * @return the forecast
		 */
		@JsonSetter("forecast")
		public Forecast getForecast() {
			return forecast;
		}

		/**
		 * @param forecast
		 *            the forecast to set
		 */
		@JsonSetter("forecast")
		public void setForecast(final Forecast forecast) {
			this.forecast = forecast;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Weather [location=" + location + ", forecast=" + forecast + "]";
		}

	}

	@JsonPropertyOrder({ "name", "country", "dtz" })
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Location {
		private String town;
		private String country;
		private String timeZone;

		@JsonGetter("name")
		public String getTown() {
			return town;
		}

		@JsonSetter("name")
		public void setTown(final String town) {
			this.town = town;
		}

		@JsonGetter("country")
		public String getCountry() {
			return country;
		}

		@JsonSetter("country")
		public void setCountry(final String country) {
			this.country = country;
		}

		/**
		 * @return the timeZone
		 */
		@JsonGetter("tzn")
		public String getTimeZone() {
			return timeZone;
		}

		/**
		 * @param timeZone
		 *            the timeZone to set
		 */
		@JsonSetter("tzn")
		public void setTimeZone(final String timeZone) {
			this.timeZone = timeZone;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Location [town=" + town + ", country=" + country + ", timeZone=" + timeZone
					+ "]";
		}

	}

	@JsonPropertyOrder({ "intervals" })
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Forecast {
		private Interval[] intervals;

		/**
		 * @return the intervals
		 */
		@JsonGetter("intervals")
		public Interval[] getIntervals() {
			return intervals;
		}

		/**
		 * @param intervals
		 *            the intervals to set
		 */
		@JsonSetter("intervals")
		public void setIntervals(final Interval[] intervals) {
			this.intervals = intervals;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Forecast [intervals=" + Arrays.toString(intervals) + "]";
		}

	}

	@JsonPropertyOrder({ "dateTime", "intervalLength", "prrr", "tt" })
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Interval {
		private String date;
		private int intervalLength;
		private int rain;
		private float temperature;

		/**
		 * @return the date
		 */
		@JsonGetter("dateTime")
		public String getDate() {
			return date;
		}

		/**
		 * @param date
		 *            the date to set
		 */
		@JsonSetter("dateTime")
		public void setDate(final String date) {
			this.date = date;
		}

		/**
		 * 
		 * @param date
		 *            the date to set
		 */
		public void setDate(final Date date) {
			this.date = DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
		}

		/**
		 * @return the date as Date
		 */
		public Date getDateAsDate() {
			final TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(date);
			final Instant i = Instant.from(ta);
			return Date.from(i);
		}

		/**
		 * @return the intervalLength
		 */
		@JsonGetter("intervalLength")
		public int getIntervalLength() {
			return intervalLength;
		}

		/**
		 * @param intervalLength
		 *            the intervalLength to set
		 */
		@JsonSetter("intervalLength")
		public void setIntervalLength(final int intervalLength) {
			this.intervalLength = intervalLength;
		}

		/**
		 * @return the rain
		 */
		@JsonGetter("prrr")
		public int getRain() {
			return rain;
		}

		/**
		 * @param rain
		 *            the rain to set
		 */
		@JsonSetter("prrr")
		public void setRain(final int rain) {
			this.rain = rain;
		}

		/**
		 * @return the temperature
		 */
		@JsonGetter("tt")
		public float getTemperature() {
			return temperature;
		}

		/**
		 * @param temperature
		 *            the temperature to set
		 */
		@JsonSetter("tt")
		public void setTemperature(final float temperature) {
			this.temperature = temperature;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			setDate(getDateAsDate());
			return "Interval [date=" + date + ", intervalLength=" + intervalLength + ", rain="
					+ rain + ", temperature=" + temperature + "]";
		}
	}

	/**
	 * @return the weather
	 */
	@JsonGetter("LocationWeather")
	public Weather getWeather() {
		return weather;
	}

	/**
	 * @param weather
	 *            the weather to set
	 */
	@JsonSetter("LocationWeather")
	public void setWeather(final Weather weather) {
		this.weather = weather;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocationWeather [weather=" + weather + "]";
	}

}
