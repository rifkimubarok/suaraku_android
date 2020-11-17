package net.soradigital.suaraku.model;

public class Images{
	private String types;
	private String uniqId;
	private String filename;
	private String updatedAt;
	private String createdAt;
	private int id;

	public void setTypes(String types){
		this.types = types;
	}

	public String getTypes(){
		return types;
	}

	public void setUniqId(String uniqId){
		this.uniqId = uniqId;
	}

	public String getUniqId(){
		return uniqId;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	public String getFilename(){
		return filename;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}
