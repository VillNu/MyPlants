package nu.vill.myplants.model;

import java.util.ArrayList;

import android.util.Log;

public class Patch {
	
	private int id = 0;
	private String name = "";
	private String desc = "";
	private ArrayList<Plant> plants = null;
	
	/**
	 * Constructor for creating a new Patch (id not known)
	 * @param name
	 * @param desc
	 */
	public Patch(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	/**
	 * Constructor for existing Patch, all parameters known
	 * @param name
	 * @param desc
	 * @param plants
	 */
	public Patch(int id, String name, String desc, ArrayList<Plant> plants) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.plants = plants;
	}
	
	/**
	 * Add plant
	 * @param plant
	 */
	void addPlant(Plant plant){
		
		try {
			//nu.vill.myplants.server.InternetData.createPlant(this, plant);
			plants.add(plant);
		}catch(Exception e){
			Log.e("model_update","Error adding plant to patch : " + e.toString());
		}
	}
	
	void ReadPlant(Plant plant){}
	void UpdatePlant(Plant plant){}
	void DeletePlant(Plant plant){}
	
	
	// Getters and setters
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getDesc() {return desc;}
	public void setDesc(String desc) {this.desc = desc;}
	public ArrayList<Plant> getPlants() {return plants;}
	public void setPlants(ArrayList<Plant> plants) {this.plants = plants;}
	
	
	
	
	
	

}
