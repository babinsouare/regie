package com.api.regie.repository;

import com.api.regie.models.NotificationJournal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationJournalRepository extends JpaRepository<NotificationJournal, UUID> {

    List<NotificationJournal> findAll();

    /** Garde anti-doublon : la même règle ne s'applique qu'une fois par objet et par canal. */
    boolean existsByParametrageIdAndReferenceObjetAndCanal(UUID parametrageId, UUID referenceObjet, String canal);

    List<NotificationJournal> findByReferenceObjetOrderByDtCreatedDesc(UUID referenceObjet);

    List<NotificationJournal> findByEvenementOrderByDtCreatedDesc(String evenement);

    List<NotificationJournal> findByDtCreatedBetweenOrderByDtCreatedDesc(Date dateDebut, Date dateFin);

    List<NotificationJournal> findAllByOrderByDtCreatedDesc(Pageable pageable);

    NotificationJournal save(NotificationJournal journal);

    Optional<NotificationJournal> findById(UUID id);
}
