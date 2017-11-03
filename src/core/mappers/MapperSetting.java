package core.mappers;

public class MapperSetting {
	public static enum SettingType{
		//Normal Mapper settings
		PRG_data,CHR_data, BatteryExists,Mirroring,
		//NSF player settings
		NSF_data,BankSwitch,ExtraSoundChips,DataPlayAddr,
		DataInitAddr,DataLoadAddr,PlaySpeed,StartSong,TotalSongs,
		TuneRegion,SongName,ArtistName,
		//NSFe Additional Settings
		TrackNames,TrackTimes,FadeTimes,AuthInfo
	}
	
	public SettingType setting;
	public Object value;
	public MapperSetting(SettingType type, Object val){
		setting = type;
		value = val;
	}
}
