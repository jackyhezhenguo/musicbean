package com.musicbean.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.reflect.Field;
import java.util.Map;

public class PreferencesManager {

    private static Editor mEditor = null;
    
    private Object settingObject = null;
    private Object settingObjectBak = null;
    private Map<String, ?> mMemberMap = null;
    private static PreferencesManager mSettingsManager = null;
    private static SharedPreferences mSharedPreferences = null;
    
    public static PreferencesManager getInstance() {
        if (mSettingsManager == null) {
            mSettingsManager = new PreferencesManager();
        }
        return mSettingsManager;
    }

    public static Object getSettings() {
        return getInstance().settingObject;
    }

    public void loadSettings(Context context, Class<?> settingsClass, String settingsfilename) {
        try {
            settingObject = settingsClass.newInstance();
            settingObjectBak = settingsClass.newInstance();
            mSharedPreferences = context.getSharedPreferences((settingsfilename == null) ? "settings" : settingsfilename,
                    Context.MODE_PRIVATE);
            if (mSharedPreferences != null) {
                mEditor = mSharedPreferences.edit();
                mMemberMap = mSharedPreferences.getAll();
                Field[] fields = settingsClass.getDeclaredFields();
                for(Field field : fields){
					Object o = mMemberMap.get(field.getName());
					if (o != null) {
						if (field != null) {
							try {
								field.set(settingObject, o);
								field.set(settingObjectBak, o);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					} else {
						mMemberMap.put(field.getName(), null);
					}
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sync() {
        if ((settingObject != null) && (settingObjectBak != null) && (mMemberMap != null)) {
            for (String key : mMemberMap.keySet()) {
            	sync(key);
            }
        }
    }

    protected void sync(String key) {
        if ((settingObject != null) && (settingObjectBak != null)) {
            try {
            	Field field = settingObject.getClass().getDeclaredField(key);
                Object obj = field.get(settingObject);
                Object objbak = field.get(settingObjectBak);
                if (obj != null && !obj.equals(objbak)) {
                    field.set(settingObjectBak, obj);
                    if (mEditor != null) {
                        if (field.getType().equals(int.class)) {
                            mEditor.putInt(key, (Integer) obj);
                        } else if (field.getType().equals(long.class)) {
                            mEditor.putLong(key, (Long) obj);
                        } else if (field.getType().equals(byte.class)) {
                            mEditor.putInt(key, (Byte) obj);
                        } else if (field.getType().equals(float.class)) {
                            mEditor.putFloat(key, (Float) obj);
                        } else if (field.getType().equals(double.class)) {
                            mEditor.putFloat(key, (Float) obj);
                        } else if (field.getType().equals(char.class)) {
                            char value = (Character) obj;
                            mEditor.putInt(key, value);
                        } else if (field.getType().equals(String.class)) {
                            mEditor.putString(key, (String) obj);
                        } else if (field.getType().equals(byte[].class)) {
                            mEditor.putString(key, (String) obj);
                        } else if (field.getType().equals(boolean.class)) {
                            mEditor.putBoolean(key, (Boolean) obj);
                        } else if (field.getType().equals(short.class)) {
                            mEditor.putInt(key, (Short) obj);
                        }
                        mEditor.commit();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
