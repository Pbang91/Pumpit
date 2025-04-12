package com.example.pumpit.global.log;

import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class LogContextDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> ctxMap = LogContext.getContextMap();

        return () -> {
            try {
                if (ctxMap != null) {
                    LogContext.setContextMap(ctxMap);
                }

                runnable.run();
            } finally {
                // Clear the context map after the task is executed
                LogContext.clear();
            }
        };
    }
}
