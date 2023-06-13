package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private LinkedList<Task> tasks = new LinkedList<>();
    private LinkedList<Task> cotyOfTasks;
    private static final int MAXIMUM_TASKS_IN_HISTORY = 10;

    public void add(Task task) { /*Добавление в метод истории происходит только в том случае, если в методе get...()
                                  будет пройдена проверка на нахождение задачи с заданным идентификатором в мапе */
        tasks.addFirst(task);
        if (tasks.size()>MAXIMUM_TASKS_IN_HISTORY) {
            tasks.remove(MAXIMUM_TASKS_IN_HISTORY-1);
        }
    }

    public List<Task> getHistory() {
        cotyOfTasks = (LinkedList) tasks.clone();
        return cotyOfTasks;
    }

}
