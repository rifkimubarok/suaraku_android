package net.soradigital.suaraku.model;

public class Candidate {
	private int CAN_INDEX;
	private Images images;
	private String CAN_CODE;
	private String CAN_STATUS;
	private String CAN_VISIMISI;
	private String CAN_IMAGE;
	private String CAN_SHORTNAME;
	private String CAN_POPULAR;
	private String CAN_PROMOSI;
	private String CAN_LONGNAME;
	private String CAN_KOWIL;
	private String CAN_SERIAL;
	private String CAN_DEFAULT;
	private String PEM_CODE;

	public void setCANINDEX(int CAN_INDEX){
		this.CAN_INDEX = CAN_INDEX;
	}

	public int getCANINDEX(){
		return CAN_INDEX;
	}

	public void setImages(Images images){
		this.images = images;
	}

	public Images getImages(){
		return images;
	}

	public void setCANCODE(String CAN_CODE){
		this.CAN_CODE = CAN_CODE;
	}

	public String getCANCODE(){
		return CAN_CODE;
	}

	public void setCANSTATUS(String CAN_STATUS){
		this.CAN_STATUS = CAN_STATUS;
	}

	public String getCANSTATUS(){
		return CAN_STATUS;
	}

	public void setCANVISIMISI(String CAN_VISIMISI){
		this.CAN_VISIMISI = CAN_VISIMISI;
	}

	public String getCANVISIMISI(){
		return CAN_VISIMISI;
	}

	public void setCANIMAGE(String CAN_IMAGE){
		this.CAN_IMAGE = CAN_IMAGE;
	}

	public String getCANIMAGE(){
		return CAN_IMAGE;
	}

	public void setCANSHORTNAME(String CAN_SHORTNAME){
		this.CAN_SHORTNAME = CAN_SHORTNAME;
	}

	public String getCANSHORTNAME(){
		return CAN_SHORTNAME;
	}

	public void setCANPOPULAR(String CAN_POPULAR){
		this.CAN_POPULAR = CAN_POPULAR;
	}

	public String getCANPOPULAR(){
		return CAN_POPULAR;
	}

	public void setCANPROMOSI(String CAN_PROMOSI){
		this.CAN_PROMOSI = CAN_PROMOSI;
	}

	public String getCANPROMOSI(){
		return CAN_PROMOSI;
	}

	public void setCANLONGNAME(String CAN_LONGNAME){
		this.CAN_LONGNAME = CAN_LONGNAME;
	}

	public String getCANLONGNAME(){
		return CAN_LONGNAME;
	}

	public void setCANKOWIL(String CAN_KOWIL){
		this.CAN_KOWIL = CAN_KOWIL;
	}

	public String getCANKOWIL(){
		return CAN_KOWIL;
	}

	public void setCANSERIAL(String CAN_SERIAL){
		this.CAN_SERIAL = CAN_SERIAL;
	}

	public String getCANSERIAL(){
		return CAN_SERIAL;
	}

	public void setCANDEFAULT(String CAN_DEFAULT){
		this.CAN_DEFAULT = CAN_DEFAULT;
	}

	public String getCANDEFAULT(){
		return CAN_DEFAULT;
	}

	public void setPEMCODE(String PEM_CODE){
		this.PEM_CODE = PEM_CODE;
	}

	public String getPEMCODE(){
		return PEM_CODE;
	}
}
