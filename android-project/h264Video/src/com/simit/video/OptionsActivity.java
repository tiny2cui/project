

package com.simit.video;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class OptionsActivity extends PreferenceActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final Preference videoEnabled = findPreference("stream_video");
        final Preference videoEncoder = findPreference("video_encoder");
        final Preference videoResolution = findPreference("video_resolution");
        final Preference videoBitrate = findPreference("video_bitrate");
        final Preference videoFramerate = findPreference("video_framerate");
        final Preference audioEnabled = findPreference("stream_audio");
        final Preference audioEncoder = findPreference("audio_encoder");
        
        videoEncoder.setEnabled(settings.getBoolean("stream_video", true));
        audioEncoder.setEnabled(settings.getBoolean("stream_audio", true));
        
        videoResolution.setSummary("Current resolution is "+settings.getInt("video_resX", 640)+"x"+settings.getInt("video_resY", 320)+"px");
        videoFramerate.setSummary("Current framerate is "+Integer.parseInt(settings.getString("video_framerate", "15"))+"fps");
        videoBitrate.setSummary("Current bitrate is "+Integer.parseInt(settings.getString("video_bitrate", "500"))+"kbps");
        
        videoResolution.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		Editor editor = settings.edit();
        		Pattern pattern = Pattern.compile("([0-9]+)x([0-9]+)");
        		Matcher matcher = pattern.matcher((String)newValue);
        		matcher.find();
        		editor.putInt("video_resX", Integer.parseInt(matcher.group(1)));
        		editor.putInt("video_resY", Integer.parseInt(matcher.group(2)));
        		editor.commit();
        		videoResolution.setSummary("Current resolution is "+(String)newValue+"px");
        		return true;
			}
        });
        
        videoFramerate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		videoFramerate.setSummary("Current framerate is "+(String)newValue+"fps");
        		return true;
			}
        });

        videoBitrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		videoBitrate.setSummary("Current bitrate is "+(String)newValue+"kbps");
        		return true;
			}
        });
        
        videoEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		boolean state = (Boolean)newValue;
        		videoEncoder.setEnabled(state);
        		videoResolution.setEnabled(state);
        		videoBitrate.setEnabled(state);
        		videoFramerate.setEnabled(state);
        		return true;
			}
        });
        
        audioEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		boolean state = (Boolean)newValue;
        		audioEncoder.setEnabled(state);
        		return true;
			}
        });
        
    }
    
}
