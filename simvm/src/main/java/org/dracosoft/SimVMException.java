package org.dracosoft;

public class SimVMException extends RuntimeException {
    public SimVMException(SimVM vm, String message) {
        super(message + " at " + vm.runPointDesc());
    }
}
