package router;

public class RouteEvent {
    
    private int[] EVs = new int[6];
    private String eventTitle;
    private String eventLocation;
    private RouteEvent prev;
    private RouteEvent next;

    /*
     * Default constructor for the RouteEvent class
     * This should only be called for the start of the route.
     */
    public RouteEvent() {
        for (int i = 0; i < 6; i++) {
            this.EVs[i] = 0;
        }
        this.eventTitle = "Start Run";
        this.eventLocation = "Pallet Town";
        this.prev = null;
        this.next = null;
    }

    /*
     * Constructor for the RouteEvent class parameters: 
     * RouteEvent prev: the previous RouteEvent in the list
     * String title: the name of the event
     * String loc: the location of the event
     */
    public RouteEvent(RouteEvent prev, String title, String loc) {
        for (int i = 0; i < 6; i++) {
            this.EVs[i] = prev.getEVs()[i];
        }
        this.eventTitle = title;
        this.eventLocation = loc;
        this.prev = prev;
        this.next = prev.getNext();
        prev.setNext(this);
    }

    // Getters and setters

    public int[] getEVs() {
        return this.EVs;
    }

    public void setEVs(int[] EVs) {
        this.EVs = EVs;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return this.eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public RouteEvent getPrev() {
        return this.prev;
    }

    public void setPrev(RouteEvent prev) {
        this.prev = prev;
    }

    public RouteEvent getNext() {
        return this.next;
    }

    public void setNext(RouteEvent next) {
        this.next = next;
    }

}
