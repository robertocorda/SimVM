package org.dracosoft;


import java.util.*;

public class SimVM {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Restituisce il valore della variabile Vn (1..9).
     */
    public int getVariable(int n) {
        if (n < 1 || n >= 9) {
            throw new SimVMException(this, "Indice variabile fuori range (1..9)");
        }
        return variables[n - 1];
    }

    private int instrSizeLoaded() {
        return instructions.size();
    }

    public String runPointDesc() {
        return "pc:" + pc + " instr:" + getCurrentInstrRaw();
    }

    private String getCurrentInstr() {

        if (pc < 0) {
            return "";
        }

        if (!isInRangePc()) {
            throw new SimVMException(this, "Indice istruzione non nel range (1.." + instrSizeLoaded() + ")");
        }

        return instructions.get(pc);
    }

    private String getCurrentInstrRaw() {
        if (!isInRangePc()) {
            return "# ERROR # Indice variabile non nel range (1.." + instrSizeLoaded() + ")";
        }

        return instructions.get(pc);
    }

    private boolean isInRangePc() {
        return pc >= 0 && pc < instrSizeLoaded();
    }


    // Direzioni possibili per MOVE/ROTATE
    public enum Direction {
        NORD, SUD, EST, OVEST
    }

    // Struttura per gestire lo stack (CALL/RETURN)
    private static class StackFrame {
        int returnAddress;
        List<Integer> arguments;

        StackFrame(int ra, List<Integer> args) {
            this.returnAddress = ra;
            this.arguments = args;
        }
    }

    // Stato della VM
    private int x = 0;                  // posizione X
    private int y = 0;                  // posizione Y
    private Direction direction = Direction.NORD;
    private int[] variables = new int[9];   // V1..V9 (in array [0..8])
    private int pc = 0;                    // program counter

    private List<String> instructions = new ArrayList<>();
    private Map<String, Integer> labelMap = new HashMap<>();
    private Stack<StackFrame> callStack = new Stack<>();


    public int getPc() {
        return pc;
    }

    // Carica il programma: analizza righe, riconosce label e istruzioni.
    public void loadProgram(List<String> programLines) {
        instructions.clear();
        labelMap.clear();
        callStack.clear();
        pc = 0;

        int instructionIndex = 0;
        for (String line : programLines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                // Riga vuota o commento
                continue;
            }
            // Se la riga finisce con ":", è una label
            if (isaLabel(line)) {
                String label = line.substring(0, line.length() - 1).trim();
                labelMap.put(label, instructionIndex);
                // Non incrementiamo instructionIndex qui, la label "nona" un'istruzione eseguibile
            } else {
                // Istruzione
                instructions.add(line);
                instructionIndex++;
            }

        }
    }

    private boolean isaLabel(String line) {
        String[] tokens = splitTokens(line);

        if (tokens.length == 1) {
            return endsWithColon(line);
        } else if (tokens.length > 1) {
            if (endsWithColon(line)) {
                throw new LoadVMException(this, "LABEL must be a single word and end with colon '" + line + "'");
            }
            return false;
        }

        return false;
    }

    private static boolean endsWithColon(String line) {
        return line.endsWith(":");
    }

    public void run() {
        while (true) {
            StepResult result = runNext();

            if (isDebugMode()) {
                System.out.println(result);
                statusDebug();
            }

            if (result.isTerminated()) {
                System.out.println("PROGRAMMA TERMINATO.");
                break;
            }
        }
    }

    private boolean isDebugMode() {
        return true;
    }


    public StepResult runNext() {
        // 1. Verifichiamo se il programma è terminato
        if (isNowTerminated("")) {
            return new StepResult(
                    true,    // terminated
                    "",      // no instruction
                    "END",   // commandName
                    new HashMap<>(), // variablesUsed
                    null,    // labelName
                    null     // ifDebugInfo
            );
        }

        String instr = getCurrentInstr();


        String[] tokens = splitTokens(instr);
        String command = tokens[0];

        // Raccogli variabili usate:
        Map<String, Integer> varsUsed = new HashMap<>();
        for (String t : tokens) {
            if (isaVariable(t)) {
                varsUsed.put(t, readValue(t));
            }
        }

        // Esegui l'istruzione
        boolean incrementPc = execute(instr);

        // Se l'istruzione non ha già modificato pc, lo incrementiamo
        if (incrementPc) {
            pc++;
        }

        // Verifichiamo se ora il programma è terminato
        boolean nowTerminated = isNowTerminated(command);

        // Creiamo costruttore di StepResult con campi extra
        // - labelName => se l'istruzione è una label (dipende dal tuo design)
        // - ifDebugInfo => se l'istruzione era "IF"
        String labelName = null;
        StepResult.IfDebugInfo ifDebug = null;

        if (command.equals("IF")) {
            // es. "IF V1 < 10 JUMP END:"
            // tokens[1] = "V1", tokens[2] = "<", tokens[3] = "10", tokens[5] = "END:"
            String varToken = tokens[1];
            String op = tokens[2];
            String valToken = tokens[3];
            String jumpLabel = tokens[5];
            int varValue = readValue(varToken);
            int compareValue = parseInteger(valToken);
            boolean conditionTrue = evaluateCondition(varValue, op, compareValue);

            // Creiamo l'oggetto ifDebug
            ifDebug = new StepResult.IfDebugInfo(
                    varToken,
                    varValue,
                    op,
                    compareValue,
                    conditionTrue,
                    jumpLabel
            );
        }

        // Nel caso tu avessi deciso di includere le label come istruzioni "LABEL <nome>"
        // potresti controllare if (command.equals("LABEL")), e così via.

        // Ora costruiamo l'oggetto StepResult
        StepResult result = new StepResult(
                nowTerminated,
                instr,
                command,
                varsUsed,
                labelName,
                ifDebug
        );

        return result;
    }

    private boolean isNowTerminated(String command) {

        if ("HALT".equals(command)) {
            return true;
        }

        return pc >= instrSizeLoaded() || pc < 0;
    }

    private static String[] splitTokens(String instr) {
        if (instr == null) {
            return new String[0];
        }
        return instr.split("\\s+");
    }

    private boolean isaVariable(String t) {
        // Riconosciamo "V" + 1 cifra? Oppure "V" + due cifre?
        // Dipende da quante variabili hai. Esempio per 1..9:
        return t.matches("^V[1-9]$");
    }


    /**
     * Esegue la singola istruzione.
     * Restituisce true se bisogna incrementare pc dopo l'esecuzione,
     * false se l'istruzione ha già modificato pc (es: salto, call...).
     */
    private boolean execute(String instr) {
        // Facciamo lo split
        String[] tokens = splitTokens(instr);
        String command = tokens[0];

        switch (command) {
            case "MOVE":
                doMove();
                return executeReturn(true);

            case "EAT":
                doEat();
                statusDebug();
                return executeReturn(true);

            case "LOOK":
                doLook();
                statusDebug();
                return executeReturn(true);

            case "ROTATE":
                if (tokens.length < 2) {
                    throw new SimVMException(this, "ROTATE <NORD/SUD/EST/OVEST> mancante.");
                }
                doRotate(tokens[1]);
                return executeReturn(true);

            case "WAIT":
                if (tokens.length < 2) {
                    throw new SimVMException(this, "WAIT <millisecondi> mancante.");
                }
                int ms = parseInteger(tokens[1]);
                doWait(ms);
                return executeReturn(true);

            case "IF": {
                boolean update = handleIfJump(tokens, instr);
                return executeReturn(update);
            }

            case "CALL": {
                boolean update = handleCall(tokens, instr);
                return executeReturn(update);
            }

            case "RETURN":
                boolean retVal = doReturn();
                return executeReturn(retVal);

            case "ADD":
                // Sintassi: ADD Vdest Op1 Op2
                if (tokens.length < 4) {
                    throw new SimVMException(this, "ADD Vdest var/int var/int");
                }
                doAdd(tokens[1], tokens[2], tokens[3]);

                return executeReturn(true);

            case "SUB":
                // Sintassi: SUB Vdest Op1 Op2
                if (tokens.length < 4) {
                    throw new SimVMException(this, "SUB Vdest var/int var/int");
                }
                doSub(tokens[1], tokens[2], tokens[3]);

                return executeReturn(true);

            case "LET":
                // Sintassi: LET <dest> <value|var>
                // Esempi validi:
                //   LET V1 10
                //   LET V1 V2
                if (tokens.length < 3) {
                    throw new SimVMException(this, "Uso di LET errato. Esempio: LET V1 10");
                }
                doLetAssignment(tokens[1], tokens[2]);
                return executeReturn(true);

            case "JUMP":
                // sintassi: JUMP <label>
                if (tokens.length < 2) {
                    throw new SimVMException(this, "Manca la label. Sintassi: JUMP <label>");
                }
                doJump(tokens[1]);         // metodo che imposta pc = labelMap.get(label)

                return executeReturn(false);

            case "HALT":
                pc = -1;
                return executeReturn(false);

            default:
                throw new SimVMException(this, "Comando sconosciuto: " + command);

        }

    }

    private void doJump(String label) {
        if (!labelMap.containsKey(label)) {
            throw new SimVMException(this, "Label non trovata: " + label);
        }
        // Imposta il pc all'istruzione associata alla label
        this.pc = labelMap.get(label);
    }

    private void doLetAssignment(String destToken, String srcToken) {
        // 1. Verifica che destToken sia "V1".. "V9"
        if (!isaVariable(destToken)) {
            throw new SimVMException(this,
                    "DEST non è una variabile valida: " + destToken);
        }
        int destIdx = parseVariableIndex(destToken);

        // 2. Leggiamo il srcToken (o variabile, o intero)
        int srcValue = readValue(srcToken);

        // 3. Assegnazione
        variables[destIdx] = srcValue;
    }


    private boolean executeReturn(boolean update) {
        doSomethingBeforeGotoNextInstr();
        return update;
    }

    private void doSomethingBeforeGotoNextInstr() {
        System.out.println("--->" + getCurrentInstrRaw());
    }

    // ---------- Implementazione IF <var> <op> <val> JUMP <label> ----------
    private boolean handleIfJump(String[] tokens, String instr) {
        // Formato: IF V1 < 10 JUMP MIA_LABEL
        // tokens = ["IF", "V1", "<", "10", "JUMP", "MIA_LABEL"]
        if (tokens.length < 6) {
            throw new SimVMException(this, "Sintassi IF <var> <op> <val> JUMP <label> errata ");
        }
        String varToken = tokens[1];
        String op = tokens[2];
        String valToken = tokens[3];
        String jumpWord = tokens[4];
        String label = tokens[5];

        if (!jumpWord.equals("JUMP")) {
            throw new SimVMException(this, "Manca 'JUMP' in IF ... JUMP ");
        }

        int varValue = readValue(varToken);   // leggi la variabile (o numero)
        int compareValue = parseInteger(valToken);

        boolean condition = evaluateCondition(varValue, op, compareValue);
        if (condition) {
            // Salto
            if (!labelMap.containsKey(label)) {
                throw new SimVMException(this, "Label non esistente: " + label);
            }
            pc = labelMap.get(label);
            return false; // pc già modificato
        } else {
            return true; // continuiamo
        }
    }

    private boolean evaluateCondition(int lhs, String op, int rhs) {
        switch (op) {
            case "==":
                return lhs == rhs;
            case "!=":
                return lhs != rhs;
            case "<":
                return lhs < rhs;
            case ">":
                return lhs > rhs;
            case "<=":
                return lhs <= rhs;
            case ">=":
                return lhs >= rhs;
            default:
                throw new SimVMException(this, "Operatore condizione non valido: " + op);
        }
    }

    // ---------- Gestione CALL <label> <args...> / RETURN ----------
    private boolean handleCall(String[] tokens, String instr) {
        // CALL MIAFUNZIONE 10 20 ...
        if (tokens.length < 2) {
            throw new SimVMException(this, "CALL <funzione> [argomenti...]");
        }
        String funcName = tokens[1];
        List<Integer> args = new ArrayList<>();
        for (int i = 2; i < tokens.length; i++) {
            args.add(parseInteger(tokens[i]));
        }
        if (!labelMap.containsKey(funcName)) {
            throw new SimVMException(this, "Funzione/label non trovata: " + funcName);
        }
        // Creiamo uno stack frame
        StackFrame frame = new StackFrame(pc + 1, args);
        callStack.push(frame);
        // Saltiamo alla label
        pc = labelMap.get(funcName);

        return false;
    }

    private boolean doReturn() {
        if (callStack.isEmpty()) {
            // Nessuna funzione da cui tornare
            System.out.println("=> [RETURN] Stack di chiamate vuoto, nessun salto.");
            return true; // prosegui normalmente
        }
        StackFrame frame = callStack.pop();
        pc = frame.returnAddress;
        return false; // pc impostato
    }

    // ---------- ADD / SUB ----------
    // Esempio: ADD V1 V2 10 => V1 = (valore di V2) + 10
    //          SUB V1 40 V2 => V1 = 40 - (valore di V2)
    private void doAdd(String destToken, String op1Token, String op2Token) {
        int destIdx = parseVariableIndex(destToken);
        int val1 = readValue(op1Token);
        int val2 = readValue(op2Token);
        variables[destIdx] = val1 + val2;
    }

    private void doSub(String destToken, String op1Token, String op2Token) {
        int destIdx = parseVariableIndex(destToken);
        int val1 = readValue(op1Token);
        int val2 = readValue(op2Token);
        variables[destIdx] = val1 - val2;
    }

    // Legge un token che può essere "V1".."V9" oppure un numero intero
    private int readValue(String token) {
        if (isaVariable(token)) {
            int idx = parseVariableIndex(token);
            return variables[idx];
        } else {
            return parseInteger(token);
        }
    }

    // Parse variabile "V1" -> 0, "V2" -> 1, ...
    private int parseVariableIndex(String varToken) {
        if (!isaVariable(varToken)) {
            throw new SimVMException(this, "Nome variabile non valido: " + varToken);
        }
        int num = parseInteger(varToken.substring(1));
        if (num < 1 || num > 9) {
            throw new SimVMException(this, "Indice variabile fuori range (1..9): " + varToken);
        }
        return num - 1;
    }

    private int parseInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new SimVMException(this, "Valore non numerico: " + s);
        }
    }

    // ---------- Implementazione comandi di "movimento/azione" fittizi ----------
    private void doMove() {
        switch (direction) {
            case NORD:
                y -= 1;
                break;
            case SUD:
                y += 1;
                break;
            case EST:
                x += 1;
                break;
            case OVEST:
                x -= 1;
                break;
        }
    }

    private void doEat() {
        System.out.println("=> [EAT] Mangio davanti a (" + x + "," + y + ") (demo)");
    }

    private void doLook() {
        System.out.println("=> [LOOK] Osservo davanti a me (demo).");
    }

    private void doRotate(String dir) {
        switch (dir) {
            case "NORD":
                direction = Direction.NORD;
                break;
            case "SUD":
                direction = Direction.SUD;
                break;
            case "EST":
                direction = Direction.EST;
                break;
            case "OVEST":
                direction = Direction.OVEST;
                break;
            default:
                throw new SimVMException(this, "Direzione non valida: " + dir);
        }
    }

    private void doWait(int ms) {
        System.out.println("=> [WAIT] Attendo " + ms + " ms...");
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Stampa di debug dopo ogni istruzione
    private void statusDebug() {

        // Esempio: uD'unica riga di debug con tutti i dati principali
        // [DEBUG] CMD="MOVE", PC=2, (x=0,y=1), Dir=NORD, V1=10, V2=3, V3=0, stackDepth=1

        StringBuilder sb = new StringBuilder();
        sb.append("[STATUS] ");
        sb.append("PC=").append(pc).append(", ");
        sb.append("(x=").append(x).append(",y=").append(y).append("), ");
        sb.append("Dir=").append(direction).append(", ");

        // Stampo V1..V9 in sequenza
        for (int i = 0; i < 9; i++) {
            sb.append("V").append(i + 1).append("=").append(variables[i]);
            if (i < 8) {
                sb.append(", ");
            }
        }

        // Dimensione dello stack di chiamate (o altre info di interesse)
        sb.append(", stackDepth=").append(callStack.size());

        System.out.println(sb);
    }


    // ---------- Esempio di utilizzo ----------
    public static void main(String[] args) {
        // Esempio di programma:
        // 1) Imposta V1=10, V2=3
        // 2) Esegue V3 = V1 + V2  (ADD V3 V1 V2)
        // 3) Esegue V1 = V1 - 5   (SUB V1 V1 5)
        // 4) Se V1 < 5 => salta a LABEL FINE
        // 5) Chiama la "funzione" TESTFUNC
        // 6) WAIT 200
        // 7) [FINE:] label
        // 8) Ritorna

        List<String> program = new ArrayList<>();
        program.add("# DEMO con ADD e SUB");
        program.add("V1 10");        // V1=10
        program.add("V2 3");         // V2=3
        program.add("ADD V3 V1 V2"); // V3 = 10 + 3 = 13
        program.add("SUB V1 V1 5");  // V1 = 10 - 5 = 5
        program.add("IF V1 < 5 JUMP FINE:");
        program.add("CALL TESTFUNC 99");
        program.add("WAIT 200");
        program.add("FINE:");        // label
        // Definizione "funzione" TESTFUNC
        program.add("TESTFUNC:");
        program.add("MOVE");
        program.add("RETURN");

        // Carichiamo ed eseguiamo
        SimVM vm = new SimVM();
        vm.loadProgram(program);
        vm.run();
        vm.statusDebug();
    }
}

