package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.CommunityLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommunityLinkRepository extends JpaRepository<CommunityLink, UUID> {

}
