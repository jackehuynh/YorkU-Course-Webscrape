package webscrape;

public class Section {

    private String sectionLetter;
    private String times;
    private String location;
    private String instructor;
    private String classType;
    private String catNumber;
    private String info;

    private Section(Builder course) {
        this.times = course.times;
        this.location = course.location;
        this.instructor = course.instructor;
        this.classType = course.classType;
        this.catNumber = course.catNumber;
        this.sectionLetter = course.sectionLetter;
        this.info = course.info;
    }

    public static class Builder {

        private String sectionLetter;
        private String catNumber;
        private String times;
        private String location;
        private String instructor;
        private String classType; // Lab, tutorial, lecture, etc...
        private String info;

        public Builder(String sectionLetter) {
            this.sectionLetter = sectionLetter;
        }

        public Section build() {
            return new Section(this);
        }

        public Builder setTime(String time) {
            this.times = time;
            return this;
        }

        public Builder addInfo(String info) {
            this.info = info;
            return this;
        }

        public Builder setInstructor(String prof) {
            this.instructor = prof;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setClassType(String type) {
            this.classType = type;
            return this;
        }

        public Builder setCatNumber(String catNumber) {
            this.catNumber = catNumber;
            return this;
        }
    }

    @Override
    public String toString() {
        return sectionLetter + " " + classType
                + " " + instructor
                + " " + catNumber + " " + times
                + " " + info;
    }
}
