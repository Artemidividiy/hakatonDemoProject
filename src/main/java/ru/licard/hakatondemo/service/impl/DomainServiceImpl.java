package ru.licard.hakatondemo.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.licard.hakatondemo.data.HakatonEntityRepository;
import ru.licard.hakatondemo.domain.HakatonEntity;
import ru.licard.hakatondemo.dto.SendingDto;
import ru.licard.hakatondemo.service.DomainService;

@Service
@RequiredArgsConstructor
@EnableTransactionManagement
public class DomainServiceImpl implements DomainService {

    private final HakatonEntityRepository hakatonEntityRepository;

    // этот метод проверяет что записи с таким ID в БД нет,
    // после чего записывает новую запись

    // т. к. Spring параллелит процессы самостоятельно,
    // а проверка и запись производятся двумя разными вызовами,
    // получается конкурентность (race condition), на момент проверки
    // записи в БД нет, а на момент попытки записать уже появилась

    // я завернул каждый вызов этого метода
    // в транзакцию уровня REPEATABLE READ,
    // таким образом конкурентность устранена
    // и результаты проверки остаются актуальными
    // на момент записи

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public void saveHakatonEntity(SendingDto sendingDto) {
        if(!hakatonEntityRepository.existsHakatonEntityByName(sendingDto.getName())) {
            System.out.println(sendingDto.getName() + hakatonEntityRepository.existsHakatonEntityByName(sendingDto.getName()));
            hakatonEntityRepository.save(new HakatonEntity(sendingDto.getName()));
        }
    }
}
