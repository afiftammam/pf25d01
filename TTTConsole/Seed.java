package TTTConsole;

public enum Seed {
        CROSS("X"), NOUGHT("O"), NO_SEED(" ");

        private final String icon;

        Seed(String icon) {
                this.icon = icon;
        }

        public String getIcon() {
                return icon;
        }
}