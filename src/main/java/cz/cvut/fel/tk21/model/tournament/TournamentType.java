package cz.cvut.fel.tk21.model.tournament;

import cz.cvut.fel.tk21.model.UserRole;

public enum TournamentType {
    M, A, B, C, D, P;

    public static TournamentType getTypeFromCharacter(char type) {
        switch (type){
            case 'M': return M;
            case 'A': return A;
            case 'B': return B;
            case 'C': return C;
            case 'D': return D;
            case 'P': return P;
            default: return null;
        }
    }
}
