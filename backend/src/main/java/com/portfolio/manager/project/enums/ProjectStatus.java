package com.portfolio.manager.project.enums;

public enum ProjectStatus {
    EM_ANALISE(0),
    ANALISE_REALIZADA(1),
    ANALISE_APROVADA(2),
    INICIADO(3),
    PLANEJADO(4),
    EM_ANDAMENTO(5),
    ENCERRADO(6),
    CANCELADO(99);

    private final int sequence;

    ProjectStatus(int sequence) {
        this.sequence = sequence;
    }

    public boolean canTransitionTo(ProjectStatus targetStatus) {
        if (this == targetStatus) {
            return true;
        }

        if (targetStatus == CANCELADO) {
            return true;
        }

        if (this == CANCELADO || this == ENCERRADO) {
            return false;
        }

        return this.sequence + 1 == targetStatus.sequence;
    }
}
