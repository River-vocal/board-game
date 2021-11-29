package yuyang.hyy.game.carcassonne.core;

/**
 * Enums of directions in diagonals. Only used for monasteries.
 */
public enum DiagonalOrientation {
    TOP_LEFT {
        @Override
        public DiagonalOrientation getOpposite() {
            return DOWN_RIGHT;
        }
    }, TOP_RIGHT {
        @Override
        public DiagonalOrientation getOpposite() {
            return DOWN_LEFT;
        }
    }, DOWN_RIGHT {
        @Override
        public DiagonalOrientation getOpposite() {
            return TOP_LEFT;
        }
    }, DOWN_LEFT {
        @Override
        public DiagonalOrientation getOpposite() {
            return TOP_RIGHT;
        }
    };

    /**
     * Get the opposite direction of the original orientation.
     * @return its opposite orientation.
     */
    public abstract DiagonalOrientation getOpposite();
}
