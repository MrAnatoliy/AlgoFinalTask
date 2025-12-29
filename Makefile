JAVAC = javac
JAR = jar

JAR_NAME = heroes-student-task
SRC_DIR = src/com/heroes_task/programs
OUT_DIR = out
JAR_DIR = build
LIBS = libs/heroes_task_lib-1.0-SNAPSHOT.jar

.PHONY: all clean jar compile

all: jar

# Главная цель
jar: $(JAR_DIR)/$(JAR_NAME).jar

# Создание JAR из скомпилированных классов
$(JAR_DIR)/$(JAR_NAME).jar: compile | $(JAR_DIR)
	$(JAR) cvf $@ -C $(OUT_DIR) .

# Компиляция (всегда перекомпилирует для простоты)
compile: $(OUT_DIR)
	$(JAVAC) -cp "$(LIBS)" -d $(OUT_DIR) $(SRC_DIR)/*.java

# Создание директорий
$(OUT_DIR) $(JAR_DIR):
	mkdir -p $@

# Очистка ВСЕГО, включая build
clean:
	rm -rf $(OUT_DIR) $(JAR_DIR)