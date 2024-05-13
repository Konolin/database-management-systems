const DIRTY_WRITE_EXPLANATION =
    "Dirty write explanation\n\n" +
    "Transaction 1          Transaction 2\n" +
    "Update X -> Y\n" +
    "                                Update X -> Z\n" +
    "                                Commit Z\n" +
    "Commit Y\n\n" +
    "                Final value Y\n";

const DIRTY_READ_EXPLANATION =
    "Dirty read explanation\n\n" +
    "Transaction 1             Transaction 2\n" +
    "Read X = 10\n" +
    "Update X = 11\n" +
    "                                   If X = 11 : Update X = 9\n" +
    "                                   Commit X\n" +
    "Rollback\n" +
    "                Final value X = 10\n";

const PHANTOM_READ_EXPLANATION =
    "Phantom read explanation\n" +
    "Transaction 1             Transaction 2\n" +
    "Query X returns 2 rows\n" +
    "                                   Insert row\n" +
    "Query X returns 3 rows\n";

const UNREPEATABLE_READ_EXPLANATION = "temp - unrepeatable read explanation";

const LOST_UPDATE_EXPLANATION =
    "Lost update explanation\n\n" +
    "Transaction 1          Transaction 2\n" +
    "Update X -> Y\n" +
    "                                Update Y -> Z\n" +
    "                                Commit Z\n" +
    "Commit Y\n\n" +
    "                Final value Y\n";

export {
    DIRTY_WRITE_EXPLANATION,
    DIRTY_READ_EXPLANATION,
    PHANTOM_READ_EXPLANATION,
    UNREPEATABLE_READ_EXPLANATION,
    LOST_UPDATE_EXPLANATION
};