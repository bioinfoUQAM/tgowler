package legacy;

/**
 * Operation represents the sequence operations :
 * <ul>
 * <li>DC : Append of a Concept</li>
 * <li>AC : Addition of a Concept</li>
 * <li>SC : Specialization of a Concept</li>
 * <li>AP : Addition of a Property</li>
 * <li>SP : Specialization of a Property</li>
 * </ul>
 *
 * @author Frapin Kevin
 */
public enum Operation {
    //-------------------------------------------------------------- Operations
    DC("DC"), // ADD CONCEPT
    AC("AC"), // APPEND CONCEPT
    SC("SC"), // SPECIALIZE CONCEPT
    AP("AP"), // ADD CONCEPT
    SP("SP"); // SPECIALIZE PROPERTY

    private String symbol;

    //---------------------------------------------------------- Public methods
    /**
     * Gets the symbol associated to an operation.
     *
     * @return Symbol associated to an operation.
     */
    public String getSymbol() {
        return symbol;
    }

    //--------------------------------------------------------- Private methods
    private Operation(String symbol) {
        this.symbol = symbol;
    }
}