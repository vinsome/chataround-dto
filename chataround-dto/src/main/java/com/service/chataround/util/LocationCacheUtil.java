package com.service.chataround.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationCacheUtil {

	public static double GRID_BOX_SIZE_IN_DEGREE = 0.03;
	private static final ThreadLocal<DecimalFormat> GRID_KEY_FORMATTER = new ThreadLocal<DecimalFormat>() {
		public DecimalFormat initialValue() {
			return new DecimalFormat("0.0");
		}
	};

	public static String getGridKey(double latitude, double longitude) {
		double keyLat = roundDownToNearestLeftTopCorner(latitude,
				GRID_BOX_SIZE_IN_DEGREE);
		double keyLon = roundDownToNearestLeftTopCorner(longitude,
				GRID_BOX_SIZE_IN_DEGREE);
		/*
		 * if (keyLat >= 90.0) { keyLat = 90.0 - GRID_BOX_SIZE_IN_DEGREE; } else
		 * if (keyLat < -90.0) { keyLat = -90.0; } if (keyLon >= 180.0) { keyLon
		 * = -180.0; }
		 */

		DecimalFormat fmt = GRID_KEY_FORMATTER.get();
		return fmt.format(keyLat) + "X" + fmt.format(keyLon);
	}

	private static double roundDownToNearestLeftTopCorner(double value,
			double multiple) {
		double mod = value % multiple;
		if (mod == 0) {
			return value;
		} else if (value > 0.0) {
			return value - mod;
		} else {
			return value - mod - multiple;
		}
	}

	public static List<String> getNearestGridKeys(double latitude,
			double longitude, double gridSize) {
		double lonWest = coerceLongitudeIntoRange(longitude - gridSize);
		double lonHere = longitude;
		double lonEast = coerceLongitudeIntoRange(longitude + gridSize);
		List<String> gridKeys = new ArrayList<String>(9);
		gridKeys.add(getGridKey(latitude, lonWest));
		gridKeys.add(getGridKey(latitude, lonHere));
		gridKeys.add(getGridKey(latitude, lonEast));
		if (latitude <= (90.0 - gridSize)) {
			gridKeys.add(getGridKey(latitude + gridSize, lonWest));
			gridKeys.add(getGridKey(latitude + gridSize, lonHere));
			gridKeys.add(getGridKey(latitude + gridSize, lonEast));
		}
		if (latitude >= (-90.0 + gridSize)) {
			gridKeys.add(getGridKey(latitude - gridSize, lonWest));
			gridKeys.add(getGridKey(latitude - gridSize, lonHere));
			gridKeys.add(getGridKey(latitude - gridSize, lonEast));
		}
		return gridKeys;
	}

	/**
	 * Coerce a given longitude into the range -180.0 (exclusive) to 180.0
	 * (inclusive). This allows us to add and subtract values from a given
	 * longitude value without regard to range, and this will coerce the result
	 * into valid range.
	 */
	static double coerceLongitudeIntoRange(double longitude) {
		if (longitude > 180.0) {
			return longitude - 360.0;
		} else if (longitude <= -180.0) {
			return longitude + 360.0;
		} else {
			return longitude;
		}
	}
}
