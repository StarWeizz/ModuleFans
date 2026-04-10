package fr.modulefans.models;

public class FaqEntry {
    private int id;
    private String keywords; // comma-separated
    private String response;
    private String category;

    public FaqEntry(int id, String keywords, String response, String category) {
        this.id = id;
        this.keywords = keywords;
        this.response = response;
        this.category = category;
    }

    public int getId() { return id; }
    public String getKeywords() { return keywords; }
    public String getResponse() { return response; }
    public String getCategory() { return category; }

    public String[] getKeywordArray() {
        return keywords.toLowerCase().split(",");
    }
}
