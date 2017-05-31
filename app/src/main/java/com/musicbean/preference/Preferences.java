package com.musicbean.preference;

import java.lang.reflect.Field;

public class Preferences {
	public void sync(){
		PreferencesManager.getInstance().sync();
	}
	
	public void sync (String name){
		PreferencesManager.getInstance().sync(name);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// return super.toString();
		StringBuilder sb = new StringBuilder();
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			sb.append(field.getName());
			sb.append(":");
			try {
				sb.append(field.get(this));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
