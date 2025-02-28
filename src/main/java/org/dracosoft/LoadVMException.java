package org.dracosoft;

public class LoadVMException extends RuntimeException {
    public LoadVMException(SimVM vm, String message) {
        super(message + " while loading at " + vm.getPc());
    }
}
