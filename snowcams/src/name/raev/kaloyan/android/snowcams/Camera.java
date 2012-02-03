/***************************************************************************
 * Copyright (C) 2012  Kaloyan Raev
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***************************************************************************/
package name.raev.kaloyan.android.snowcams;

public enum Camera {
	
	ALEKO("х. Алеко", "http://cam.dir.bg/sf/c2/image.jpg"), 
	ALEKO_2("х. Алеко 2", "http://pss.bg/vremeto/cache_webcam.php?id=35"), 
	VITOSHKO_LALE("Витошко лале", "http://pss.bg/vremeto/cache_webcam.php?id=34"), 
	CHERNI_VRAH("Черни връх", "http://pss.bg/stations/kancho/cherni/webcam.jpg"), 
	VETROVALA("Ветровала", "http://pss.bg/stations/kancho/vetrovala/webcam.jpg"), 
	OFELIA("Офелия", "http://pss.bg/stations/kancho/ofelia/webcam.jpg"), 
	OFELIA_2("Офелия 2", "http://pss.bg/stations/kancho/ofelia/webcam1.jpg"), 
	BOROVETZ("Боровец", "http://media.borovets-bg.com/cams/channel?channel=41"), 
	SITNIAKOVO("Ситняково", "http://media.borovets-bg.com/cams/channel?channel=61"), 
	SKI_RUNS_YASTREBETZ("писти Ястребец", "http://media.borovets-bg.com/cams/channel?channel=11"), 
	FINAL_YASTREBETZ_3("финал Ястребец 3", "http://media.borovets-bg.com/cams/channel?channel=21"), 
	POPANGELOV("писта Попангелов", "http://media.borovets-bg.com/cams/channel?channel=51"), 
	YASTREBETZ_HUT("х. Ястребец", "http://media.borovets-bg.com/cams/channel?channel=81"), 
	MARKUDZHIK_2("Маркуджик 2", "http://media.borovets-bg.com/cams/channel?channel=71"), 
//	MUSALA("вр. Мусала", "http://guest:guest@beo-db.inrne.bas.bg:8888/cgi-bin/viewer/video.jpg"),
	MALYOVITZA_HUT("х. Мальовица", "http://62.73.67.230:2001/image.jpg"), 
	SKI_RUNS_MALYOVITZA("ски зона Мальовица", "http://62.73.67.235/IMAGE.JPG"), 
	RILA_LAKES("х. Рилски езера", "http://www.rilskiezera.bg/meteo/rilskiezerahut.jpg"), 
	PIONERSKA("х. Пионерска", "http://www.rilskiezera.bg/cam_ds/cam_ds.jpg"), 
	PLATOTO("Платото", "http://www.banskoski.com/images/livecam/livecam4-bg.jpg"), 
	SHILIGARNIKA("Шилигарника", "http://www.banskoski.com/images/livecam/livecam3-bg.jpg"), 
	BANDERISHKA_POLYANA("Бъндеришка поляна", "http://www.banskoski.com/images/livecam/livecam1-bg.jpg"), 
	TODORKA("Тодорка", "http://www.banskoski.com/images/livecam/livecam2-bg.jpg"), 
	CHALIN_VALOG("Чалин валог", "http://www.banskoski.com/images/livecam/livecam6-bg.jpg"), 
	BANSKO("Банско", "http://www.banskoski.com/images/livecam/livecam5-bg.jpg"), 
	GOTZE_DELCHEV("х. Гоце Делчев", "http://www.stringmeteo.com/stations/gotsehut/webcamimage.jpg"), 
	BEZBOG("Безбог", "http://www.stringmeteo.com/stations/bezbog/webcamimage.jpg"), 
	CHEPELARE("Чепеларе", "http://84.54.155.86:8040/oneshotimage.jpg"), 
	MECHI_CHAL("Мечи чал", "http://84.54.155.86:8030/oneshotimage.jpg"), 
//	ROZHEN("Рожен", "http://sob.nao-rozhen.org/sites/default/files/cameras/outdoor.jpg"), 
//	PERELIK("х. Перелик", "http://www.rodopite.bg:8080/oneshotimage.jpg"), 
	ZDRAVETZ_HUT("х. Здравец", "http://www.vremeto.org/zdravetz/zdravetz-hut.jpg"), 
	ZDRAVETZ_HOTEL("хотел Здравец", "http://www.hotelzdravetz.com/wdisplay/webcam000M.jpg"), 
	KOVACHEVITZA("Ковачевица", "http://webcam.kovachevitsa-tavern.com/images/video.jpg"), 
	KOPRIVKI("Копривки", "http://212.91.164.28:8080/cam_1.jpg"), 
	DERMENKA("х. Дерменка", "http://www.stamb.net/dermenka/dermenka.jpg"), 
	PLEVEN("х. Плевен", "http://hpleven-camera.pladi.bg:2004/IMAGE.JPG"), 
	BEKLEMETO("Беклемето", "http://www.hotelsima.com/meteo/cam0.jpg"), 
//	DOBRILA("х. Добрила", "http://www.dobrila.eu/meteo/dobrila.jpg"), 
	KOM("х. Ком", "http://pss.bg/stations/kancho/kom/video.jpg"), 
	UZANA("Узана", "http://193.68.123.246/cgi-bin/viewer/video.jpg"); 
//	UZANA_2("Узана 2", "http://193.68.123.245/axis-cgi/mjpg/video.cgi?resolution=640x480");
	
	private String label;
	private String url;
	
	private Camera(String label, String url) {
		this.label = label;
		this.url = url;
	}
	
	public String label() {
		return label;
	}
	
	public String url() {
		return url;
	}
	
	public static int count() {
		return Camera.values().length;
	}
	
	public static String labelFor(int index) {
		return Camera.values()[index].label();
	}
	
	public static String urlFor(int index) {
		return Camera.values()[index].url();
	}

}
