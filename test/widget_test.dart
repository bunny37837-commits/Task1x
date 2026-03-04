import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_test/flutter_test.dart';
import 'package:taskremind_pro/main.dart';

void main() {
  testWidgets('shows progress then startup error instead of blank screen', (
    tester,
  ) async {
    final completer = Completer<AppState>();

    await tester.pumpWidget(AppBootstrap(loadAppState: () => completer.future));
    expect(find.byType(CircularProgressIndicator), findsOneWidget);

    completer.completeError(Exception('boot failed'));
    await tester.pumpAndSettle();

    expect(find.text('Failed to start app'), findsOneWidget);
    expect(find.textContaining('boot failed'), findsOneWidget);
  });
}
