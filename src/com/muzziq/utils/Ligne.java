package src.com.muzziq.utils;

public class Ligne {
	
	private String name;
	private String titre;
	private String album;
	private String genre;
	private String annee;

	
	public Ligne(String n, String t, String a, String g, String an)
	{
		this.name = n;
		this.titre = t;
		this.album = a;
		this.genre = g;
		this.annee = an;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getAnnee() {
		return annee;
	}
	public void setAnnee(String annee) {
		this.annee = annee;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public String toString()
	{
		String sortie = name+" | "+titre+" | "+album+" | "+genre+" | "+annee+"\n";
		return sortie;
	}
	
}
