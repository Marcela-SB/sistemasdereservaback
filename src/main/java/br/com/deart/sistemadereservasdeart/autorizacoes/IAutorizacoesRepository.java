package br.com.deart.sistemadereservasdeart.autorizacoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAutorizacoesRepository extends JpaRepository<AutorizacoesModel, UUID> {
}
