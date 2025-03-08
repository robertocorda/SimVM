
# "Simula", idea generale
Un "mondo" in cui le entità "simbioma" programmabili in cui possono interagire (spostare, vedere, assorbire energia... da decidere)
Altra possibilità: potrebbero essere delle entità dnd che seguono le regole dnd.



# Simbioma
delle entità che posso avere in input un programma scritto nel linguaggio Simvm che le fa muovere ed interagire

* SIMBIOMA entità che agisce in un ENVIRONMENT
* ENVIRONMENT auto esplicativo (l'acquario per il pesce)
* RULE è una CONDITION più un COMMAND piu un WEIGHT (importance)
* CONDITION è una condizione sulla base dei sensi ed eventualmente su altri dati sulla base della quale può essere lanciato il command.
* COMMAND è qualcosa che bioma vuole fare
* ACTION è qualcosa che succede nell'enviroment
* INTENT è il COMMAND che viene passato a sé stessi o all'enviroment, può o meno avere successo a seconda dello stato dell'environment

# ESEMPI/DOC
## Differenza tra commannd e action
COMMAND: PUSH il simbioma che vuole andare in una certa direzione
INTENT contiene PUSH
ACTION MOVE effettivamente muove il simbioma (il command PUSH può o meno determinare il movimento del SIMBIOMA)

## Esempio di RULE
if applies {
    if see object with
        distance < 5 and
        color is RED
        and speed > 2
} do {
    ROTATE
} with importance {
    100 - (distance * 10) + (speed * 2)
}


# @Deprecated
# Simvm 
microlanguage VM pseudo assembly
una virtual machine per un microlinguaggio in stile assembly





