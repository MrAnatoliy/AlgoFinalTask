#!/bin/bash
set -e # Exit on error

heroes_jar_dir="/home/tttolik/MIFI_Magistracy/Semestr_1/A_and_DS/heroes/jars"
builded_jar_dir="/home/tttolik/MIFI_Magistracy/Semestr_1/A_and_DS/heroes_student_task/build"
game_dir="/home/tttolik/MIFI_Magistracy/Semestr_1/A_and_DS/heroes"
game_jar="Heroes Battle-1.0.0.jar"

if [[ ! -d "$heroes_jar_dir" ]]; then
    echo "Ошибка: директория $heroes_jar_dir не существует"
    exit 1
fi

if [[ ! -d "$builded_jar_dir" ]]; then
    echo "Ошибка: директория $builded_jar_dir не существует"
    exit 1
fi

# Сборка
echo "🧹 Очистка..."
make clean

echo "🔨 Сборка проекта..."
make all

# Проверка, что JAR создался
jar_file="$builded_jar_dir/heroes-student-task.jar"
if [[ ! -f "$jar_file" ]]; then
    echo "Ошибка: JAR-файл не найден по пути $jar_file"
    exit 1
fi

# Безопасное копирование
echo "📦 Копирование JAR в $heroes_jar_dir..."
rm -f "$heroes_jar_dir"/*  # -f не ругается на пустую директорию
cp "$jar_file" "$heroes_jar_dir/"

echo "✅ Успешно!"

echo "▶️ Запуск игры с пересобраной библиотекой"
cd "$game_dir"
java -jar "$game_jar"