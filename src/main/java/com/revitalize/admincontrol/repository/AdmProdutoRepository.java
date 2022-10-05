package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmProdutoRepository extends CrudRepository<AdmProdutoModel, UUID> {



}
