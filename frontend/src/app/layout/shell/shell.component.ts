import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'fq-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="flex min-h-screen bg-[var(--fq-bg)]">
      <fq-sidebar />
      <main class="flex-1 overflow-auto">
        <section class="mx-auto w-full max-w-7xl px-4 py-6 md:px-8">
          <router-outlet />
        </section>
      </main>
    </div>
  `,
})
export class ShellComponent {}
