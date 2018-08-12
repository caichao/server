% 
clc;
clear all;
close all;

addpath('..\results');
addpath('..\debug');

% groundtruth = [1.444, 1.144];
groundtruth = [1.480, 1.888];
locations = load('localization_2018-07-22_14_15_24.txt');
pairwise_distance = load('beepbeep.txt');
figure;
subplot(211); plot(locations(:, 1), 'r', 'linewidth', 3); title('coordinates of x');ylabel('meters');
subplot(212); plot(locations(:, 2), 'b', 'linewidth', 3); title('coordinates of y');ylabel('meters');

figure;
scatter(locations(:, 1), locations(:, 2),'filled','r'); hold on;
scatter(mean(locations(:, 1)), mean(locations(:, 2)), 'b+','linewidth', 3); hold on;
scatter(groundtruth(1), groundtruth(2), 'filled', 'g'); hold off;
xlim([0, 2.86]);
ylim([0, 2.71]);
legend('esimated', 'average', 'groundtruth');
error = (mean(locations(:, 1)) - groundtruth(1))^2;
error = error + (mean(locations(:, 2)) - groundtruth(2))^2;
title(['error = ', num2str(sqrt(error)), 'm']);
xlabel('meters');
ylabel('meters');

figure;
subplot(311); 
one = pairwise_distance(pairwise_distance(:, 4) == 1, 1); 
plot(one, 'r', 'linewidth', 3); hold on;
one = pairwise_distance(pairwise_distance(:, 4) == 1, 2); 
plot(one, 'b', 'linewidth', 3); hold off;
legend('estimated', 'groundtruth');
title('distance between anchor1 and anchor0');
ylabel('meters');
subplot(312); 
two = pairwise_distance(pairwise_distance(:, 4) == 2 & pairwise_distance(:, 5) == 1, 1); 
plot(two, 'r', 'linewidth', 3); hold on;
two = pairwise_distance(pairwise_distance(:, 4) == 2 & pairwise_distance(:, 5) == 1, 2) - 0.3; 
plot(two, 'b', 'linewidth', 3); hold off;
legend('estimated', 'groundtruth');
title('distance between anchor2 and anchor1');
ylabel('meters');
subplot(313);
three = pairwise_distance(pairwise_distance(:, 4) == 3 & pairwise_distance(:, 5) == 2, 1); 
plot(three, 'r', 'linewidth', 3); hold on;
three = pairwise_distance(pairwise_distance(:, 4) == 3 & pairwise_distance(:, 5) == 2, 2) - 0.2; 
plot(three, 'b', 'linewidth', 3); hold off;
legend('estimated', 'groundtruth');
title('distance between anchor3 and anchor2');
ylabel('meters');