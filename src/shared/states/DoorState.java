package shared.states;

public enum DoorState {
    Open {
        @Override
        public String toString() {
            return "Open";
        }
    },
    Closed {
        @Override
        public String toString() {
            return "Closed";
        }
    }
}
