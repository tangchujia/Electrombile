/**
 * 
 */
package com.syxgo.electrombile.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AMapUtil {
	private final static double a=6378245.0;
	private final static double pi=3.14159265358979324;
	private final static double ee=0.00669342162296594626;

	/**
	 * 判断edittext是否null
	 */
	public static String checkEditText(EditText editText) {
		if (editText != null && editText.getText() != null
				&& !(editText.getText().toString().trim().equals(""))) {
			return editText.getText().toString().trim();
		} else {
			return "";
		}
	}

	public static Spanned stringToSpan(String src) {
		return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
	}

	public static String colorFont(String src, String color) {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<font color=").append(color).append(">").append(src)
				.append("</font>");
		return strBuf.toString();
	}

	public static String makeHtmlNewLine() {
		return "<br />";
	}

	public static String makeHtmlSpace(int number) {
		final String space = "&nbsp;";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < number; i++) {
			result.append(space);
		}
		return result.toString();
	}

	public static String getFriendlyLength(int lenMeter) {
		if (lenMeter > 10000) // 10 km
		{
			int dis = lenMeter / 1000;
			return dis + ChString.Kilometer;
		}

		if (lenMeter > 1000) {
			float dis = (float) lenMeter / 1000;
			DecimalFormat fnum = new DecimalFormat("##0.0");
			String dstr = fnum.format(dis);
			return dstr + ChString.Kilometer;
		}

		if (lenMeter > 100) {
			int dis = lenMeter / 50 * 50;
			return dis + ChString.Meter;
		}

		int dis = lenMeter / 10 * 10;
		if (dis == 0) {
			dis = 10;
		}

		return dis + ChString.Meter;
	}

	public static boolean IsEmptyOrNullString(String s) {
		return (s == null) || (s.trim().length() == 0);
	}

	/**
	 * 把LatLng对象转化为LatLonPoint对象
	 */
	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
		return new LatLonPoint(latlon.latitude, latlon.longitude);
	}

	/**
	 * 把LatLonPoint对象转化为LatLon对象
	 */
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}

	/**
	 * GPS 坐标转高德
	 * @param context
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static LatLng GpsConvertToGD(Context context, double lat, double lng){
		CoordinateConverter converter = new CoordinateConverter(context);
		converter.from(CoordinateConverter.CoordType.GPS);
		converter.coord(new LatLng(lat, lng));
		return converter.convert();
	}

	/**
	 * GPS latLng坐标转高德
	 * @param context
	 * @param latLng
	 * @return
	 */
	public static LatLng GpsConvertToGD(Context context, LatLng latLng){
		CoordinateConverter converter = new CoordinateConverter(context);
		converter.from(CoordinateConverter.CoordType.GPS);
		converter.coord(latLng);
		return converter.convert();
	}

	/**
	 * 把集合体的LatLonPoint转化为集合体的LatLng
	 */
	public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
		for (LatLonPoint point : shapes) {
			LatLng latLngTemp = AMapUtil.convertToLatLng(point);
			lineShapes.add(latLngTemp);
		}
		return lineShapes;
	}

	/**
	 * long类型时间格式化
	 */
	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}

	public static final String HtmlBlack = "#000000";
	public static final String HtmlGray = "#808080";
	
	public static String getFriendlyTime(int second) {
		if (second > 3600) {
			int hour = second / 3600;
			int miniate = (second % 3600) / 60;
			return hour + "小时" + miniate + "分钟";
		}
		if (second >= 60) {
			int miniate = second / 60;
			return miniate + "分钟";
		}
		return second + "秒";
	}
	
	//路径规划方向指示和图片对应


		public static String getBusPathTitle(BusPath busPath) {
			if (busPath == null) {
				return String.valueOf("");
			}
			List<BusStep> busSetps = busPath.getSteps();
			if (busSetps == null) {
				return String.valueOf("");
			}
			StringBuffer sb = new StringBuffer();
			for (BusStep busStep : busSetps) {
				 StringBuffer title = new StringBuffer();
			   if (busStep.getBusLines().size() > 0) {
				   for (RouteBusLineItem busline : busStep.getBusLines()) {
					   if (busline == null) {
							continue;
						}
					  
					   String buslineName = getSimpleBusLineName(busline.getBusLineName());
					   title.append(buslineName);
					   title.append(" / ");
				}
//					RouteBusLineItem busline = busStep.getBusLines().get(0);
				   
					sb.append(title.substring(0, title.length() - 3));
					sb.append(" > ");
				}
				if (busStep.getRailway() != null) {
					RouteRailwayItem railway = busStep.getRailway();
					sb.append(railway.getTrip()+"("+railway.getDeparturestop().getName()
							+" - "+railway.getArrivalstop().getName()+")");
					sb.append(" > ");
				}
			}
			return sb.substring(0, sb.length() - 3);
		}

		public static String getBusPathDes(BusPath busPath) {
			if (busPath == null) {
				return String.valueOf("");
			}
			long second = busPath.getDuration();
			String time = getFriendlyTime((int) second);
			float subDistance = busPath.getDistance();
			String subDis = getFriendlyLength((int) subDistance);
			float walkDistance = busPath.getWalkDistance();
			String walkDis = getFriendlyLength((int) walkDistance);
			return String.valueOf(time + " | " + subDis + " | 步行" + walkDis);
		}
		
		public static String getSimpleBusLineName(String busLineName) {
			if (busLineName == null) {
				return String.valueOf("");
			}
			return busLineName.replaceAll("\\(.*?\\)", "");
		}

	public static LatLonPoint toWGS84Point(double latitude, double longitude) {
		LatLonPoint dev = calDev(latitude, longitude);
		double retLat = latitude - dev.getLatitude();
		double retLon = longitude - dev.getLongitude();
		dev = calDev(retLat, retLon);
		retLat = latitude - dev.getLatitude();
		retLon = longitude - dev.getLongitude();
		return new LatLonPoint(retLat, retLon);
	}

	private static LatLonPoint calDev(double wgLat,double wgLon){
		if(isOutofChina(wgLat,wgLon)){
			return new LatLonPoint(0,0);
		}
		double dLat=calLat(wgLon-105.0,wgLat-35.0);
		double dLon=calLon(wgLon-105.0, wgLat-35.0);
		double radLat=wgLat/180.0*pi;
		double magic=Math.sin(radLat);
		magic=1-ee*magic*magic;
		double sqrtMagic=Math.sqrt(magic);
		dLat=(dLat*180.0)/((a*(1-ee))/(magic*sqrtMagic)*pi);
		dLon=(dLon*180.0)/(a/sqrtMagic*Math.cos(radLat)*pi);
		return new LatLonPoint(dLat,dLon);
	}
	private static boolean isOutofChina(double lat, double lon) {
		if(lon<72.004 || lon>137.8347){
			return true;
		}
		if(lat<0.8293 || lat>55.8271){
			return true;
		}

		return false;
	}

	private static double calLat(double x, double y) {
		double ret=-100.0+2.0*x+3.0*y+0.2*y*y+0.1*x*y+0.2*Math.sqrt(Math.abs(x));
		ret +=(20.0*Math.sin(6.0*x*pi)+20.0*Math.sin(2.0*x*pi))*2.0/3.0;
		ret +=(20.0*Math.sin(y*pi)+40.0*Math.sin(y/3.0*pi))*2.0/3.0;
		ret +=(160.0*Math.sin(y/12.0*pi)+320*Math.sin(y*pi/30.0))*2.0/3.0;

		return ret;
	}
	private static double calLon(double x,double y){
		double ret=300.0+x+2.0*y+0.1*x*x+0.1*x*y+0.1*Math.sqrt(Math.abs(x));
		ret +=(20.0*Math.sin(6.0*x*pi)+20.0*Math.sin(2.0*x*pi))*2.0/3.0;
		ret +=(20.0*Math.sin(x*pi)+40.0*Math.sin(x/3.0*pi))*2.0/3.0;
		ret +=(150.0*Math.sin(x/12.0*pi)+300.0*Math.sin(x/30.0*pi))*2.0/3.0;;

		return ret;

	}

}
