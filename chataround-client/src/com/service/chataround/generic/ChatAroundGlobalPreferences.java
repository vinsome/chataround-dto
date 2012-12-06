package com.service.chataround.generic;

import com.service.chataround.dto.PreferencesDto;

public abstract class ChatAroundGlobalPreferences {
	public abstract void savePreferences(PreferencesDto dto);
	public abstract PreferencesDto getPreferences();
	
}
