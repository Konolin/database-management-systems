const DIRTY_WRITE_EXPLANATION =
    "Dirty write explanation\n\n" +
    "Transaction 1          Transaction 2\n" +
    "Update X -> Y\n" +
    "                                Update X -> Z\n" +
    "                                Commit Z\n" +
    "Commit Y\n\n" +
    "                Final value Y\n";

const DIRTY_READ_EXPLANATION = "temp - dirty read explanation";
const PHANTOM_READ_EXPLANATION = "temp - phantom read explanation";
const UNREPEATABLE_READ_EXPLANATION = "temp - unrepeatable read explanation";
const LOST_UPDATE_EXPLANATION = "temp - lost update explanation";

export {
    DIRTY_WRITE_EXPLANATION,
    DIRTY_READ_EXPLANATION,
    PHANTOM_READ_EXPLANATION,
    UNREPEATABLE_READ_EXPLANATION,
    LOST_UPDATE_EXPLANATION
};