import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
    canActivate: [guestGuard],
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(
        (m) => m.RegisterComponent
      ),
    canActivate: [guestGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'workouts',
        loadComponent: () =>
          import('./features/workouts/workouts.component').then(
            (m) => m.WorkoutsComponent
          ),
      },
      {
        path: 'nutrition',
        loadComponent: () =>
          import('./features/nutrition/nutrition.component').then(
            (m) => m.NutritionComponent
          ),
      },
      {
        path: 'challenges',
        loadComponent: () =>
          import('./features/challenges/challenges.component').then(
            (m) => m.ChallengesComponent
          ),
      },
      {
        path: 'leaderboard',
        loadComponent: () =>
          import('./features/leaderboard/leaderboard.component').then(
            (m) => m.LeaderboardComponent
          ),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then(
            (m) => m.ProfileComponent
          ),
      },
      {
        path: 'social',
        loadComponent: () =>
          import('./features/social/social.component').then((m) => m.SocialComponent),
      },
      {
        path: 'notifications',
        loadComponent: () =>
          import('./features/notifications/notifications.component').then(
            (m) => m.NotificationsComponent
          ),
      },
      {
        path: 'ai-coach',
        loadComponent: () =>
          import('./features/ai-coach/ai-coach.component').then(
            (m) => m.AiCoachComponent
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
