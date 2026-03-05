import 'dart:async';

import 'package:android_alarm_manager_plus/android_alarm_manager_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter_overlay_window/flutter_overlay_window.dart';
import 'package:path/path.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sqflite/sqflite.dart';

const _overlayTaskTitleKey = 'overlay_task_title';
const _overlayTaskIdKey = 'overlay_task_id';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await AndroidAlarmManager.initialize();

  final db = await AppDatabase.instance;
  final settings = await SettingsStore.instance;
  final appState = AppState(db, settings);
  await appState.load();

  runApp(ChangeNotifierProvider.value(value: appState, child: const TaskRemindApp()));
}

@pragma('vm:entry-point')
void overlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MaterialApp(debugShowCheckedModeBanner: false, home: OverlayScreen()));
}

@pragma('vm:entry-point')
Future<void> alarmCallback(int taskId) async {
  final db = await AppDatabase.instance;
  final settings = await SettingsStore.instance;
  final task = await db.getTask(taskId);
  if (task == null || !task.isEnabled || !settings.globalEnabled) return;

  await settings.cacheOverlayData(task.id!, task.title);
  if (!await FlutterOverlayWindow.isPermissionGranted()) return;

  await FlutterOverlayWindow.showOverlay(
    enableDrag: false,
    overlayTitle: 'Task Reminder',
    overlayContent: task.title,
    flag: OverlayFlag.defaultFlag,
    visibility: NotificationVisibility.visibilityPublic,
    height: WindowSize.matchParent,
    width: WindowSize.matchParent,
  );

  await ReminderScheduler.scheduleTask(task);
}

class OverlayScreen extends StatefulWidget {
  const OverlayScreen({super.key});

  @override
  State<OverlayScreen> createState() => _OverlayScreenState();
}

class _OverlayScreenState extends State<OverlayScreen> {
  String title = 'Task reminder';
  int? taskId;
  double progress = 1;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      title = prefs.getString(_overlayTaskTitleKey) ?? 'Task reminder';
      taskId = prefs.getInt(_overlayTaskIdKey);
    });
    _timer = Timer.periodic(const Duration(milliseconds: 100), (timer) async {
      final next = progress - (1 / 120);
      if (next <= 0) {
        await FlutterOverlayWindow.closeOverlay();
        timer.cancel();
      } else {
        setState(() => progress = next);
      }
    });
  }

  Future<void> _done() async {
    if (taskId != null) {
      final db = await AppDatabase.instance;
      final task = await db.getTask(taskId!);
      if (task != null) {
        await db.updateTask(task.copyWith(isEnabled: false));
        await ReminderScheduler.cancelTask(taskId!);
      }
    }
    await FlutterOverlayWindow.closeOverlay();
  }

  Future<void> _snooze() async {
    if (taskId != null) {
      final when = DateTime.now().add(const Duration(minutes: 10));
      await AndroidAlarmManager.oneShotAt(when, taskId!, alarmCallback, exact: true, wakeup: true);
    }
    await FlutterOverlayWindow.closeOverlay();
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black87,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(title, style: const TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              LinearProgressIndicator(value: progress),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(child: FilledButton(onPressed: _done, child: const Text('Done'))),
                  const SizedBox(width: 8),
                  Expanded(child: FilledButton(onPressed: _snooze, child: const Text('Snooze 10m'))),
                  const SizedBox(width: 8),
                  Expanded(
                    child: FilledButton(
                      onPressed: () async => FlutterOverlayWindow.closeOverlay(),
                      child: const Text('Dismiss'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class TaskRemindApp extends StatelessWidget {
  const TaskRemindApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<AppState>(
      builder: (context, appState, _) {
        return MaterialApp(
          title: 'TaskRemind Pro',
          debugShowCheckedModeBanner: false,
          themeMode: appState.darkMode ? ThemeMode.dark : ThemeMode.light,
          darkTheme: ThemeData.dark(useMaterial3: true),
          theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.indigo),
          home: const HomeScreen(),
        );
      },
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final state = context.watch<AppState>();
    return Scaffold(
      appBar: AppBar(title: const Text('TaskRemind Pro')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            SwitchListTile(
              title: const Text('Global reminder toggle'),
              value: state.globalEnabled,
              onChanged: state.toggleGlobal,
            ),
            SwitchListTile(
              title: const Text('Dark mode'),
              value: state.darkMode,
              onChanged: state.toggleDarkMode,
            ),
            const PermissionGuide(),
            const SizedBox(height: 8),
            const TaskEditor(),
            const SizedBox(height: 8),
            Expanded(
              child: ListView.builder(
                itemCount: state.tasks.length,
                itemBuilder: (context, i) {
                  final task = state.tasks[i];
                  return Card(
                    child: ListTile(
                      title: Text(task.title),
                      subtitle: Text('Daily ${task.timeLabel}'),
                      trailing: Wrap(
                        spacing: 8,
                        children: [
                          Switch(value: task.isEnabled, onChanged: (v) => state.toggleTask(task, v)),
                          IconButton(icon: const Icon(Icons.edit), onPressed: () => TaskEditorController.of(context).edit(task)),
                          IconButton(icon: const Icon(Icons.delete), onPressed: () => state.deleteTask(task.id!)),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class PermissionGuide extends StatelessWidget {
  const PermissionGuide({super.key});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: const Text('Permissions guide'),
        subtitle: const Text('Allow overlay, exact alarms, notifications, and disable battery optimization.'),
        trailing: FilledButton(
          onPressed: () async => FlutterOverlayWindow.requestPermission(),
          child: const Text('Grant Overlay'),
        ),
      ),
    );
  }
}

class TaskEditorController extends InheritedWidget {
  const TaskEditorController({required this.edit, required super.child, super.key});
  final void Function(Task task) edit;

  static TaskEditorController of(BuildContext context) => context.dependOnInheritedWidgetOfExactType<TaskEditorController>()!;

  @override
  bool updateShouldNotify(covariant TaskEditorController oldWidget) => false;
}

class TaskEditor extends StatefulWidget {
  const TaskEditor({super.key});

  @override
  State<TaskEditor> createState() => _TaskEditorState();
}

class _TaskEditorState extends State<TaskEditor> {
  final _titleController = TextEditingController();
  TimeOfDay _time = const TimeOfDay(hour: 9, minute: 0);
  Task? _editing;

  @override
  void dispose() {
    _titleController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = context.read<AppState>();
    return TaskEditorController(
      edit: (task) {
        setState(() {
          _editing = task;
          _titleController.text = task.title;
          _time = TimeOfDay(hour: task.reminderHour, minute: task.reminderMinute);
        });
      },
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            children: [
              TextField(controller: _titleController, decoration: const InputDecoration(labelText: 'Task title')),
              const SizedBox(height: 8),
              Row(
                children: [
                  Text('Time: ${_time.format(context)}'),
                  const Spacer(),
                  TextButton(
                    onPressed: () async {
                      final picked = await showTimePicker(context: context, initialTime: _time);
                      if (picked != null) setState(() => _time = picked);
                    },
                    child: const Text('Pick time'),
                  ),
                ],
              ),
              Row(
                children: [
                  FilledButton(
                    onPressed: () async {
                      final title = _titleController.text.trim();
                      if (title.isEmpty) return;
                      if (_editing == null) {
                        await state.addTask(title, _time.hour, _time.minute);
                      } else {
                        await state.updateTask(_editing!.copyWith(
                          title: title,
                          reminderHour: _time.hour,
                          reminderMinute: _time.minute,
                        ));
                      }
                      setState(() {
                        _editing = null;
                        _titleController.clear();
                      });
                    },
                    child: Text(_editing == null ? 'Add Task' : 'Save Task'),
                  ),
                  if (_editing != null) ...[
                    const SizedBox(width: 8),
                    OutlinedButton(
                      onPressed: () => setState(() {
                        _editing = null;
                        _titleController.clear();
                      }),
                      child: const Text('Cancel'),
                    ),
                  ],
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class AppState extends ChangeNotifier {
  AppState(this._db, this._settings);
  final AppDatabase _db;
  final SettingsStore _settings;

  List<Task> tasks = [];
  bool darkMode = false;
  bool globalEnabled = true;

  Future<void> load() async {
    tasks = await _db.getTasks();
    darkMode = _settings.darkMode;
    globalEnabled = _settings.globalEnabled;
    notifyListeners();
  }

  Future<void> addTask(String title, int hour, int minute) async {
    final id = await _db.insertTask(Task(title: title, reminderHour: hour, reminderMinute: minute, isEnabled: true));
    final task = Task(id: id, title: title, reminderHour: hour, reminderMinute: minute, isEnabled: true);
    tasks = [task, ...tasks];
    if (globalEnabled) await ReminderScheduler.scheduleTask(task);
    notifyListeners();
  }

  Future<void> updateTask(Task task) async {
    await _db.updateTask(task);
    tasks = tasks.map((e) => e.id == task.id ? task : e).toList();
    if (globalEnabled && task.isEnabled) {
      await ReminderScheduler.scheduleTask(task);
    } else {
      await ReminderScheduler.cancelTask(task.id!);
    }
    notifyListeners();
  }

  Future<void> deleteTask(int id) async {
    await _db.deleteTask(id);
    await ReminderScheduler.cancelTask(id);
    tasks.removeWhere((t) => t.id == id);
    notifyListeners();
  }

  Future<void> toggleTask(Task task, bool enabled) async => updateTask(task.copyWith(isEnabled: enabled));

  Future<void> toggleDarkMode(bool enabled) async {
    darkMode = enabled;
    await _settings.setDarkMode(enabled);
    notifyListeners();
  }

  Future<void> toggleGlobal(bool enabled) async {
    globalEnabled = enabled;
    await _settings.setGlobalEnabled(enabled);
    for (final t in tasks) {
      if (enabled && t.isEnabled) {
        await ReminderScheduler.scheduleTask(t);
      } else if (t.id != null) {
        await ReminderScheduler.cancelTask(t.id!);
      }
    }
    notifyListeners();
  }
}

class ReminderScheduler {
  static Future<void> scheduleTask(Task task) async {
    if (task.id == null) return;
    final now = DateTime.now();
    var next = DateTime(now.year, now.month, now.day, task.reminderHour, task.reminderMinute);
    if (!next.isAfter(now)) next = next.add(const Duration(days: 1));

    await AndroidAlarmManager.oneShotAt(next, task.id!, alarmCallback, exact: true, wakeup: true, rescheduleOnReboot: true);
  }

  static Future<void> cancelTask(int id) => AndroidAlarmManager.cancel(id);
}

class Task {
  Task({this.id, required this.title, required this.reminderHour, required this.reminderMinute, required this.isEnabled});

  final int? id;
  final String title;
  final int reminderHour;
  final int reminderMinute;
  final bool isEnabled;

  String get timeLabel => '${reminderHour.toString().padLeft(2, '0')}:${reminderMinute.toString().padLeft(2, '0')}';

  Task copyWith({int? id, String? title, int? reminderHour, int? reminderMinute, bool? isEnabled}) => Task(
        id: id ?? this.id,
        title: title ?? this.title,
        reminderHour: reminderHour ?? this.reminderHour,
        reminderMinute: reminderMinute ?? this.reminderMinute,
        isEnabled: isEnabled ?? this.isEnabled,
      );

  Map<String, Object?> toMap() => {
        'id': id,
        'title': title,
        'reminderHour': reminderHour,
        'reminderMinute': reminderMinute,
        'isEnabled': isEnabled ? 1 : 0,
      };

  static Task fromMap(Map<String, Object?> m) => Task(
        id: m['id'] as int,
        title: m['title'] as String,
        reminderHour: m['reminderHour'] as int,
        reminderMinute: m['reminderMinute'] as int,
        isEnabled: (m['isEnabled'] as int) == 1,
      );
}

class AppDatabase {
  AppDatabase._(this._db);
  final Database _db;

  static Future<AppDatabase> get instance async {
    final root = await getDatabasesPath();
    final db = await openDatabase(
      join(root, 'taskremind_pro.db'),
      version: 1,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE tasks(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            reminderHour INTEGER NOT NULL,
            reminderMinute INTEGER NOT NULL,
            isEnabled INTEGER NOT NULL
          )
        ''');
      },
    );
    return AppDatabase._(db);
  }

  Future<List<Task>> getTasks() async {
    final rows = await _db.query('tasks', orderBy: 'id DESC');
    return rows.map(Task.fromMap).toList();
  }

  Future<Task?> getTask(int id) async {
    final rows = await _db.query('tasks', where: 'id = ?', whereArgs: [id], limit: 1);
    if (rows.isEmpty) return null;
    return Task.fromMap(rows.first);
  }

  Future<int> insertTask(Task task) => _db.insert('tasks', task.toMap()..remove('id'));
  Future<void> updateTask(Task task) async => _db.update('tasks', task.toMap(), where: 'id = ?', whereArgs: [task.id]);
  Future<void> deleteTask(int id) async => _db.delete('tasks', where: 'id = ?', whereArgs: [id]);
}

class SettingsStore {
  SettingsStore._(this._prefs);
  final SharedPreferences _prefs;

  static Future<SettingsStore> get instance async => SettingsStore._(await SharedPreferences.getInstance());

  bool get darkMode => _prefs.getBool('isDarkMode') ?? false;
  bool get globalEnabled => _prefs.getBool('globalEnabled') ?? true;

  Future<void> setDarkMode(bool value) => _prefs.setBool('isDarkMode', value);
  Future<void> setGlobalEnabled(bool value) => _prefs.setBool('globalEnabled', value);

  Future<void> cacheOverlayData(int taskId, String title) async {
    await _prefs.setInt(_overlayTaskIdKey, taskId);
    await _prefs.setString(_overlayTaskTitleKey, title);
  }
}
