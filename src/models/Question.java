package models;

public class Question {
    private String text;
    private String option1;
    private String option2;
    private String option3;
    private String rightanswer;

    private static int counter = 0;
    public Question(String text, String option1, String option2, String option3, String rightanswer) {
        this.text = text;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.rightanswer = rightanswer;
        counter++;
    }


    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getOption1() {
        return option1;
    }
    public void setOption1(String option1) {
        this.option1 = option1;
    }
    public String getOption2() {
        return option2;
    }
    public void setOption2(String option2) {
        this.option2 = option2;
    }
    public String getOption3() {
        return option3;
    }
    public void setOption3(String option3) {
        this.option3 = option3;
    }
    public String getRightanswer() {
        return rightanswer;
    }
    public void setRightanswer(String rightanswer) {
        this.rightanswer = rightanswer;
    }
}
