package yuyang.hyy.game.carcassonne.core;

/**
 * Enums for the direct directions/orientations.
 */
public enum Orientation {
    TOP {
        @Override
        public Orientation getOpposite() {
            return Orientation.DOWN;
        }
    },RIGHT {
        @Override
        public Orientation getOpposite() {
            return LEFT;
        }
    }, DOWN {
        @Override
        public Orientation getOpposite() {
            return Orientation.TOP;
        }
    }, LEFT {
        @Override
        public Orientation getOpposite() {
            return Orientation.RIGHT;
        }
    }, CENTER {
        @Override
        public Orientation getOpposite() {
            return CENTER;
        }
    };

    /**
     * Get the opposite direction of the original orientation.
     * @return its opposite orientation.
     */
    public abstract Orientation getOpposite();
}
